package com.project.casaberriel.service;

import javax.mail.MessagingException;

import com.project.casaberriel.dto.EmailDto;
import com.project.casaberriel.model.reservas.ReservaEntity;
import com.project.casaberriel.model.usuarios.Usuario;

public interface IEmailService {

	public void sendMail(EmailDto email) throws MessagingException;

	void sendReservationConfirmation(ReservaEntity savedReserva,boolean cancelada,boolean modificada) throws MessagingException;
	
	public void sendUsuarioConfirmation(Usuario usuario, boolean cancelada, boolean modificada)
			throws MessagingException;

	public void sendEmail(String email, String string, String string2);



}
