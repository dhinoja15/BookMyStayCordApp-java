package com.Booking.flows;
import co.paralleluniverse.fibers.Suspendable;
import com.Booking.states.BookingState;
import net.corda.core.contracts.ContractState;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;
import static net.corda.core.contracts.ContractsDSL.requireThat;
// ******************
// * Responder flow *
// ******************
@InitiatedBy(BookingInitiatorFlow.class)
public class BookingResponderFlow extends FlowLogic<SignedTransaction> {
    private FlowSession counterpartySession;
    public BookingResponderFlow(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }
    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // Responder flow logic goes here.
        System.out.println("Booking Request received from : " + counterpartySession.getCounterparty().getName().getOrganisation());
        return subFlow(new ReceiveFinalityFlow(counterpartySession));
    }
}
