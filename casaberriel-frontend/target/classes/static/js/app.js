//*Metodo para confirmar eliminar una reserva

$('#deleteModal').on('show.bs.modal', function (event) {
	  let button = $(event.relatedTarget); // Botón que activó el modal
	  let id = button.data('id'); // Extrae la información de data-id

	  let modal = $(this);
	  let deleteUrl = '/reservas/eliminar/' + id;
	  modal.find('#confirmDeleteButton').attr('href', deleteUrl); // Asigna la URL de eliminación al botón
	});
	

function initMap() {
	// Coordenadas de tu localización (latitud y longitud)
	const location = {lat: 28.956167, lng: -13.640944}; // Reemplaza con las coordenadas reales de tu ubicación

	// Crear el mapa centrado en la localización
	const map = new google.maps.Map(document.getElementById("map"), {
		zoom: 15,
		center: location,
	});

	// Añadir un marcador en la localización
	const marker = new google.maps.Marker({
		position: location,
		map: map,
	});
}


	
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
	