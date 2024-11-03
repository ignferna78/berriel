package com.project.casaberriel.service;

import javax.mail.MessagingException;

import com.project.casaberriel.dto.EmailDto;
import com.project.casaberriel.dto.ReservaDto;
import com.project.casaberriel.model.reservas.ReservaEntity;

public interface IEmailService {

	public void sendMail(EmailDto email) throws MessagingException;

	void sendReservationConfirmation(ReservaEntity savedReserva,boolean cancelada,boolean modificada) throws MessagingException;



}
