//*Metodo para confirmar eliminar una reserva
$(document).ready(function() {
	$('#deleteModal').on('show.bs.modal', function (event) {
	  let button = $(event.relatedTarget); // Botón que activó el modal
	  let id = button.data('id'); // Extrae la información de data-id

	  let modal = $(this);
	  let deleteUrl = '/reservas/eliminar/' + id;
	  modal.find('#confirmDeleteButton').attr('href', deleteUrl); // Asigna la URL de eliminación al botón
	});
	
	
	
	
});