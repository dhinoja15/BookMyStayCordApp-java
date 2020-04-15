package com.Booking.flows;
import co.paralleluniverse.fibers.Suspendable;
import com.Booking.contracts.BookingContract;
import com.Booking.states.BookingState;
import com.sun.xml.bind.v2.model.core.ID;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import static com.Booking.contracts.BookingContract.BOOKING_CONTRACT_ID;
import java.time.Instant;
// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class BookingInitiatorFlow extends FlowLogic<SignedTransaction> {
    private final String custName;
    private final int custAge;
    private final Instant  checkInDate;
    private final Instant checkOutDate;
    private final String roomType;
    private final int roomRate;
    private final String creditCardNumber;
    private final Instant creditCardExpDate;
    private final double creditCardAmount;
    private final Party HotelHeaven;
    private final ProgressTracker.Step GENERATING_TRANSACTION = new ProgressTracker.Step("Generating transaction based on new IOU.");
    private final ProgressTracker.Step VERIFYING_TRANSACTION = new ProgressTracker.Step("Verifying contract constraints.");
    private final ProgressTracker.Step SIGNING_TRANSACTION = new ProgressTracker.Step("Signing transaction with our private key.");
    private final ProgressTracker.Step GATHERING_SIGS = new ProgressTracker.Step("Gathering the counterparty's signature.") {
    @Override
    public ProgressTracker childProgressTracker() {
          return CollectSignaturesFlow.Companion.tracker();
    }
    };
    private final ProgressTracker.Step FINALISING_TRANSACTION = new ProgressTracker.Step("Obtaining notary signature and recording transaction.") {
    @Override
    public ProgressTracker childProgressTracker() {
          return FinalityFlow.Companion.tracker();
    }
    };
    private final ProgressTracker progressTracker = new ProgressTracker(
            GENERATING_TRANSACTION,
            VERIFYING_TRANSACTION,
            SIGNING_TRANSACTION,
            GATHERING_SIGS,
            FINALISING_TRANSACTION
    );
    //private final ProgressTracker progressTracker = new ProgressTracker();
    public BookingInitiatorFlow(String custName, int custAge, Instant checkInDate, Instant checkOutDate, String roomType, int roomRate, String creditCardNumber, Instant creditCardExpDate, double creditCardAmount, Party hotelHeaven) {
        this.custName = custName;
        this.custAge = custAge;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.roomType = roomType;
        this.roomRate = roomRate;
        this.creditCardNumber = creditCardNumber;
        this.creditCardExpDate = creditCardExpDate;
        this.creditCardAmount = creditCardAmount;
        this.HotelHeaven = hotelHeaven;
    }
    @Override
    public ProgressTracker getProgressTracker() {
          return progressTracker;
    }
    @Suspendable
    @Override
    public  SignedTransaction call() throws FlowException {
    // Initiator flow logic goes here.
    if (getOurIdentity().getName().getOrganisation().equals("BookYourStay")) {
            System.out.println("Identity Verified!");
          } else {
            throw new FlowException("Booking Request not initiated by BookYourStay");
          }
     // Get Notary identity from network map
     Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
     // Create the elements for a transaction (Input/ Output states)
     BookingState outputState = new BookingState(custName,custAge,checkInDate,checkOutDate,roomType,roomRate,
                 creditCardNumber,creditCardExpDate,creditCardAmount, getOurIdentity(),HotelHeaven);
     // Transactions in Corda are built using Transaction Builder and elements are added to it
     // Stage 1.
     progressTracker.setCurrentStep(GENERATING_TRANSACTION);
     // Generate an unsigned transaction.
     TransactionBuilder txBuilder = new TransactionBuilder(notary)
                .addOutputState(outputState,BOOKING_CONTRACT_ID)
                .addCommand(new BookingContract.Booking(),getOurIdentity().getOwningKey());
      // Stage 2.
      progressTracker.setCurrentStep(VERIFYING_TRANSACTION);
      // Stage 3.
      progressTracker.setCurrentStep(SIGNING_TRANSACTION);
      // Sign the transaction.
      SignedTransaction BookingReqTxn = getServiceHub().signInitialTransaction(txBuilder);
      // Stage 4.
      progressTracker.setCurrentStep(GATHERING_SIGS);
      // Send the state to the counterparty, and receive it back with their signature.
      FlowSession bookingReqSession = initiateFlow(HotelHeaven);
      // Stage 5.
      progressTracker.setCurrentStep(FINALISING_TRANSACTION);
      // Notarise and record the transaction in both parties' vaults.
          return subFlow(new FinalityFlow(BookingReqTxn, bookingReqSession));
      }
}
