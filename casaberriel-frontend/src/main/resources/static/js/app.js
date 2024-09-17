//*Metodo para confirmar eliminar una reserva

$('#deleteModal').on('show.bs.modal', function (event) {
	  let button = $(event.relatedTarget); // Botón que activó el modal
	  let id = button.data('id'); // Extrae la información de data-id

	  let modal = $(this);
	  let deleteUrl = '/reservas/eliminar/' + id;
	  modal.find('#confirmDeleteButton').attr('href', deleteUrl); // Asigna la URL de eliminación al botón
	});

	
	function limpiarCriterios() {
	         const form = document.getElementById("formReserva");
	         const inputs = form.querySelectorAll("input, select");
	         inputs.forEach(input => {
	             if (input.type === "checkbox" || input.type === "radio") {
	                 input.checked = false;
	             } else {
	                 input.value = "";
	             }
	         });
	     }
	

