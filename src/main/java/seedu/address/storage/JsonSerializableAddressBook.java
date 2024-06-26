package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.AppointmentList;
import seedu.address.model.person.Person;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_OVERLAPPING_APPOINTMENT =
            "Appointment list contains overlapping appointment(s).";
    public static final String MESSAGE_APPOINTMENTS_PERSONS_MISMATCH = "Persons list and appointments list don't match";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedAppointment> appointments = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons and appointments.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
                                       @JsonProperty("appointments") List<JsonAdaptedAppointment> appointments) {
        this.persons.addAll(persons);
        this.appointments.addAll(appointments);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList()
                .stream()
                .map(JsonAdaptedPerson::new)
                .collect(Collectors.toList()));
        appointments.addAll(source.getAppointmentList()
                .stream()
                .map(JsonAdaptedAppointment::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();

        AppointmentList temp = new AppointmentList();
        for (JsonAdaptedAppointment jsonAdaptedAppointment : appointments) {
            Appointment appointment = jsonAdaptedAppointment.toModelType();
            temp.add(appointment);
        }

        if (temp.isOverlapping()) {
            throw new IllegalValueException(MESSAGE_OVERLAPPING_APPOINTMENT);
        }

        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();

            // check for uniqueness among persons
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }

            // check for overlapping appointments among persons
            for (Appointment ap : person.getAppointments()) {
                if (addressBook.appointmentsOverlap(ap)) {
                    throw new IllegalValueException(MESSAGE_OVERLAPPING_APPOINTMENT);
                }
            }

            // check for overlapping appointments of a person
            if (person.getAppointments().isOverlapping()) {
                throw new IllegalValueException(MESSAGE_OVERLAPPING_APPOINTMENT);
            }

            addressBook.addPerson(person);
        }

        if (!temp.asUnmodifiableObservableList().equals(addressBook.getAppointmentList())) {
            throw new IllegalValueException(MESSAGE_APPOINTMENTS_PERSONS_MISMATCH);
        }

        return addressBook;
    }

}

