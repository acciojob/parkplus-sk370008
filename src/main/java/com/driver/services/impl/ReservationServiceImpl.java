package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ParkingLotRepository;
import com.driver.repository.ReservationRepository;
import com.driver.repository.SpotRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {
    @Autowired
    UserRepository userRepository3;
    @Autowired
    SpotRepository spotRepository3;
    @Autowired
    ReservationRepository reservationRepository3;
    @Autowired
    ParkingLotRepository parkingLotRepository3;
    @Override
    public Reservation reserveSpot(Integer userId, Integer parkingLotId, Integer timeInHours, Integer numberOfWheels) throws Exception {
        //Reserve a spot in the given parkingLot such that the total price is minimum. Note that the price per hour for each spot is different
        //Note that the vehicle can only be parked in a spot having a type equal to or larger than given vehicle
        //If parkingLot is not found, user is not found, or no spot is available, throw "Cannot make reservation" exception.


        User user = userRepository3.findById(userId).get();
        ParkingLot parkingLot = parkingLotRepository3.findById(parkingLotId).get();
        boolean isSpotAvailable = false;
        List<Spot> spotList = parkingLot.getSpotList();
        SpotType vehicleType = null;
        if (numberOfWheels == 2){
            vehicleType = SpotType.TWO_WHEELER;
        } else if (numberOfWheels == 4) {
            vehicleType = SpotType.FOUR_WHEELER;
        }else {
            vehicleType = SpotType.OTHERS;
        }
        for (Spot spot : spotList){
            if (spot.getOccupied() == true  && spot.getSpotType().equals(vehicleType)){
                isSpotAvailable = true;
            }
        }

        //Exception check
        if (user == null || parkingLot == null || !isSpotAvailable){
            throw new Exception("Cannot make reservation");
        }

        //getting list of spots with given vehicle type and will sort it with according to price
        List<Spot> spotsWithGivenTypeAndSortedPrice = new ArrayList<>();
        for (Spot spot : spotList){
            if (spot.getSpotType().equals(vehicleType)){
                spotsWithGivenTypeAndSortedPrice.add(spot);
            }
        }
        //sorted arraylist in ascending order of price
        Collections.sort(spotsWithGivenTypeAndSortedPrice, Comparator.comparing(Spot::getPricePerHour));
        Reservation reservation = new Reservation();
        reservation.setNumberOfHours(timeInHours);
        reservation.setSpot(spotsWithGivenTypeAndSortedPrice.get(0));
        reservation.setUser(user);
//        reservation.setPayment();
        spotsWithGivenTypeAndSortedPrice.get(0).setOccupied(true);
        parkingLotRepository3.save(parkingLot);
        reservationRepository3.save(reservation);


        return reservation;

    }
}
