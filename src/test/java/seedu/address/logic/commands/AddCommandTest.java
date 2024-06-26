package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAppointments.SUN_APPOINTMENT_10_TO_12;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.DisjointAppointmentList;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class AddCommandTest {

    @Test
    public void constructor_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddCommand(null));
    }

    @Test
    public void execute_personAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingPersonAdded modelStub = new ModelStubAcceptingPersonAdded();
        Person validPerson = new PersonBuilder().build();

        CommandResult commandResult = new AddCommand(validPerson).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(validPerson)),
                commandResult.getFeedbackToUser());
        assertEquals(Arrays.asList(validPerson), modelStub.personsAdded);
    }

    // work on this qy
    @Test
    public void execute_nearDuplicatePerson_addNearDuplicateSuccessful() throws Exception {
        Person nearDuplicate = new PersonBuilder().withName("John Doe").build();

        // Create a Model stub that returns the near duplicate person when findNearDuplicates is called
        ModelStub modelStub = new ModelStubAcceptingPersonAddedWithNearDuplicate(nearDuplicate);

        // Create the person to be added (similar to the near duplicate)
        Person personToAdd = new PersonBuilder().withName("john   Doe  ").build();

        // Execute the AddCommand with the person to be added
        CommandResult commandResult = new AddCommand(personToAdd).execute(modelStub);

        assertEquals(String.format(AddCommand.MESSAGE_NEAR_DUPLICATES, Messages.format(personToAdd),
                nearDuplicate.getName().toString()), commandResult.getFeedbackToUser());

        assertTrue(modelStub.hasPerson(personToAdd));
    }


    @Test
    public void execute_duplicatePerson_throwsCommandException() {
        Person validPerson = new PersonBuilder().build();
        AddCommand addCommand = new AddCommand(validPerson);
        ModelStub modelStub = new ModelStubWithPerson(validPerson);

        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PERSON, () -> addCommand.execute(modelStub));
    }


    @Test
    public void execute_overlappingAppointment_throwsCommandException() {
        Person anotherPerson = new PersonBuilder(ALICE).withAppointments("11:00-13:00 SUN").build();
        AddCommand addCommand = new AddCommand(anotherPerson);
        ModelStub modelStub = new ModelStubWithAppointment(SUN_APPOINTMENT_10_TO_12);

        assertThrows(CommandException.class,
                DisjointAppointmentList.MESSAGE_CONSTRAINTS, () -> addCommand.execute(modelStub));
    }

    @Test
    public void equals() {
        Person alice = new PersonBuilder().withName("Alice").build();
        Person bob = new PersonBuilder().withName("Bob").build();
        AddCommand addAliceCommand = new AddCommand(alice);
        AddCommand addBobCommand = new AddCommand(bob);

        // same object -> returns true
        assertTrue(addAliceCommand.equals(addAliceCommand));

        // same values -> returns true
        AddCommand addAliceCommandCopy = new AddCommand(alice);
        assertTrue(addAliceCommand.equals(addAliceCommandCopy));

        // different types -> returns false
        assertFalse(addAliceCommand.equals(1));

        // null -> returns false
        assertFalse(addAliceCommand.equals(null));

        // different person -> returns false
        assertFalse(addAliceCommand.equals(addBobCommand));
    }

    @Test
    public void toStringMethod() {
        AddCommand addCommand = new AddCommand(ALICE);
        String expected = AddCommand.class.getCanonicalName() + "{toAdd=" + ALICE + "}";
        assertEquals(expected, addCommand.toString());
    }

    /**
     * A default model stub that have all but one method that add depends on failing.
     */
    private class ModelStub implements Model {
        @Override
        public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyUserPrefs getUserPrefs() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public GuiSettings getGuiSettings() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setGuiSettings(GuiSettings guiSettings) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Path getAddressBookFilePath() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBookFilePath(Path addressBookFilePath) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void addPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setAddressBook(ReadOnlyAddressBook newData) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean hasPerson(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public List<String> findNearDuplicates(Person person) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void deletePerson(Person target) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Person> getFilteredPersonList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredPersonList(Predicate<Person> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void updateFilteredAppointmentList(Predicate<Appointment> predicate) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean appointmentsOverlap(Appointment appointment) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean appointmentsOverlap(Collection<Appointment> appointments) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public ObservableList<Appointment> getFilteredAppointmentList() {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single person.
     */
    private class ModelStubWithPerson extends ModelStub {
        private final Person person;

        ModelStubWithPerson(Person person) {
            requireNonNull(person);
            this.person = person;
        }

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return this.person.isSamePerson(person);
        }

        @Override
        public boolean appointmentsOverlap(Appointment appointment) {
            return false;
        }

        @Override
        public boolean appointmentsOverlap(Collection<Appointment> appointments) {
            return false;
        }
    }

    /**
     * A Model stub that contains a single appointment.
     */
    private class ModelStubWithAppointment extends ModelStub {
        private final Appointment appointment;

        ModelStubWithAppointment(Appointment appointment) {
            requireNonNull(appointment);
            this.appointment = appointment;
        }

        @Override
        public boolean hasPerson(Person person) {
            return false;
        }

        @Override
        public boolean appointmentsOverlap(Appointment appointment) {
            requireNonNull(appointment);
            return this.appointment.overlapsWith(appointment);
        }

        @Override
        public boolean appointmentsOverlap(Collection<Appointment> appointments) {
            requireNonNull(appointments);
            for (Appointment ap : appointments) {
                if (this.appointment.overlapsWith(ap)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * A Model stub that always accept the person being added.
     */
    private class ModelStubAcceptingPersonAdded extends ModelStub {
        final ArrayList<Person> personsAdded = new ArrayList<>();

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return personsAdded.stream().anyMatch(person::isSamePerson);
        }

        @Override
        public void addPerson(Person person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }

        @Override
        public List<String> findNearDuplicates(Person person) {
            // Stub implementation, return an empty list
            return Collections.emptyList();
        }

        @Override
        public boolean appointmentsOverlap(Appointment appointment) {
            return false;
        }

        @Override
        public boolean appointmentsOverlap(Collection<Appointment> appointments) {
            return false;
        }
    }

    /**
     * A Model stub that accepts the person added and returns a near duplicate.
     */
    private class ModelStubAcceptingPersonAddedWithNearDuplicate extends ModelStub {

        final ArrayList<Person> personsAdded = new ArrayList<>();
        private final Person nearDuplicate;

        ModelStubAcceptingPersonAddedWithNearDuplicate(Person nearDuplicate) {
            requireNonNull(nearDuplicate);
            this.nearDuplicate = nearDuplicate;
        }

        @Override
        public boolean hasPerson(Person person) {
            requireNonNull(person);
            return personsAdded.stream().anyMatch(person::isSamePerson);
        }

        @Override
        public void addPerson(Person person) {
            requireNonNull(person);
            personsAdded.add(person);
        }

        @Override
        public List<String> findNearDuplicates(Person person) {
            requireNonNull(person);
            // Return the name of the near duplicate
            return Collections.singletonList(nearDuplicate.getName().toString());
        }

        @Override
        public boolean appointmentsOverlap(Appointment appointment) {
            return false;
        }

        @Override
        public boolean appointmentsOverlap(Collection<Appointment> appointments) {
            return false;
        }
    }


}
