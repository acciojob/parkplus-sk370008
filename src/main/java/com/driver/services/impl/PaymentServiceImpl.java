package com.driver.services.impl;

import com.driver.model.Payment;
import com.driver.model.PaymentMode;
import com.driver.model.Reservation;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {
        //Attempt a payment of amountSent for reservationId using the given mode ("cASh", "card", or "upi")
        //If the amountSent is less than bill, throw "Insufficient Amount" exception, otherwise update payment attributes
        //If the mode contains a string other than "cash", "card", or "upi" (any character in uppercase or lowercase), throw "Payment mode not detected" exception.
        //Note that the reservationId always exists

        Reservation reservation = reservationRepository2.findById(reservationId).get();
        int noOfHours = reservation.getNumberOfHours();
        int price = reservation.getSpot().getPricePerHour();
        int bill = noOfHours * price;
        //Exception for insufficient amount
        if (amountSent < bill){
            throw new Exception("Insufficient Amount");
        }

        mode = mode.toLowerCase();

        //Exception for payment mode
        if (!(mode.equals("cash") || mode.equals("card") || mode.equals("upi"))){
            throw new Exception("Payment mode not detected");
        }


        PaymentMode paymentMode = null;
        if (mode.equals("cash")){
            paymentMode = PaymentMode.CASH;
        } else if (mode.equals("card")) {
            paymentMode = PaymentMode.CARD;
        } else if (mode.equals("upi")) {
            paymentMode = PaymentMode.UPI;
        }



        Payment payment = new Payment();
        payment.setIsPaymentCompleted(true);
        payment.setPaymentMode(paymentMode);
        payment.setReservation(reservation);
        reservation.setPayment(payment);


        reservationRepository2.save(reservation);

        return payment;
    }
}
