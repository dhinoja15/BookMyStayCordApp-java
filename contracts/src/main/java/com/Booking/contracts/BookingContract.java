package com.Booking.contracts;
import com.Booking.states.BookingState;
import com.sun.istack.NotNull;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import org.dom4j.IllegalAddException;
import java.awt.*;
import java.security.PublicKey;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import static net.corda.core.contracts.ContractsDSL.requireThat;
// ************
// * Contract *
// ************
public class BookingContract implements Contract {
// This is used to identify our contract when building a transaction.
public static final String BOOKING_CONTRACT_ID = "com.Booking.contracts.BookingContract";
// A transaction is valid if the verify() function of the contract of all the transaction's input and output states
// does not throw an exception.
@Override
public void verify(@NotNull final LedgerTransaction tx) {
        if(tx.getCommands().size() != 1){
            throw new IllegalArgumentException("Command input can be only one") ;
        }
        Command command = tx.getCommand(0);
        CommandData commandType = command.getValue();
        List<PublicKey> reqSigners = command.getSigners();
        if(commandType instanceof Booking ){
//      Shape Rule
        if(tx.getInputStates().size() != 0) {
            throw new IllegalArgumentException("No input in Booking ");
        }
        if(tx.getOutputs().size() != 1) {
            throw new IllegalAddException("There can be only one output");
        }
//      Content Rules
        ContractState outputState = tx.getOutput(0);
        if (!(outputState instanceof BookingState )) {
            throw new IllegalArgumentException("Output state has to be of TemplateState");
        }
        BookingState bookState = (BookingState) outputState;
//      Customer Age should be greater than 18.
//      Check Out date should be greater than Check in date.
//  	After commission price should 85% of Original room price.
//      Credit Card number length should be 16.
//	    Credit Card Exp date should not be in past.
        requireThat(require -> {
                require.using("Customer's age should be more than 18", bookState.getCustAge()>= 18);
                require.using("Check out date should be after check in date", bookState.getCheckOutDate().isAfter(bookState.getCheckInDate()));
                require.using("Check in date should be future date",bookState.getCheckInDate().isAfter(Instant.now()) );
                require.using("Check out date should be future date",bookState.getCheckOutDate().isAfter(Instant.now()) );
                require.using("Room Type Must from K || NK || DD || NDD",
                        bookState.getRoomType().equals("N")||bookState.getRoomType().equals("NK")
                                ||bookState.getRoomType().equals("DD")||bookState.getRoomType().equals("NDD"));
                require.using("5\tAfter commission price should 85% of Original room price.",bookState.getCreditCardAmount() == (bookState.getRoomRate())*0.85);
                require.using("Credit card number is invalid", bookState.getCreditCardNumber().length() == 16);
                require.using("Credit card EXP Date is Invalid", bookState.getCreditCardExpDate().isAfter(Instant.now()));
                return null;
            });
//      Signer Rules
        PublicKey BookYourStayPartyKey = bookState.getBookYourStay().getOwningKey();
        if ((!reqSigners.contains(BookYourStayPartyKey))) {
                throw new IllegalArgumentException("BookYourStay party must sign the transaction");
          }
        }
    }
    // Used to indicate the transaction's intent.
    public static class Booking implements CommandData {
    }
}
