//*Metodo para confirmar eliminar una reserva

newFunction();


function newFunction() {
	$('#deleteModal').on('show.bs.modal', function(event) {
		let button = $(event.relatedTarget); // Botón que activó el modal
		let id = button.data('id'); // Extrae la información de data-id

		let modal = $(this);
		let deleteUrl = '/reservas/eliminar/' + id;
		modal.find('#confirmDeleteButton').attr('href', deleteUrl);
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

$('#comprobar-disponibilidad-formBtn').on('click', function(event) {
	event.preventDefault();

	var fechaEntrada = $('#fechaEntrada').val();
	var fechaSalida = $('#fechaSalida').val();

	console.log('Fecha de entrada enviada:', fechaEntrada);
	console.log('Fecha de salida enviada:', fechaSalida);

	var formData = {
		fechaEntrada: fechaEntrada,
		fechaSalida: fechaSalida
	};

	$.ajax({
		url: $('#comprobar-disponibilidad-form').attr('action'),
		type: 'GET',
		data: formData,
		success: function(response) {
			$('#resultados-disponibilidad').html($(response).find('#resultados-disponibilidad').html());
		},
		error: function(error) {
			console.log("Error en la comprobación de disponibilidad: ", error);
		}
	});
});


// Mostrar el botón cuando el usuario hace scroll hacia abajo 20px
window.onscroll = function() {
	mostrarBoton();
};

function mostrarBoton() {
	const btn = document.getElementById("btnSubir");
	if (document.body.scrollTop > 20 || document.documentElement.scrollTop > 20) {
		btn.style.display = "block"; // Mostrar el botón
	} else {
		btn.style.display = "none"; // Ocultar el botón
	}
}

// Volver al inicio de la página
function subirArriba() {
	document.body.scrollTop = 0; // Para Safari
	document.documentElement.scrollTop = 0; // Para Chrome, Firefox, IE y Opera
}

$(function() {
	$.fn.datepicker.dates['es'] = {
		days: ['Domingo', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado'],
		daysShort: ['Dom', 'Lun', 'Mar', 'Mié', 'Juv', 'Vie', 'Sáb'],
		daysMin: ['Do', 'Lu', 'Ma', 'Mi', 'Ju', 'Vi', 'Sá'],
		months: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'],
		monthsShort: ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun', 'Jul', 'Ago', 'Sep', 'Oct', 'Nov', 'Dic'],
		today: 'Hoy',
		clear: 'Limpiar',
		format: 'dd/mm/yyyy',
		titleFormat: "MM yyyy",
		weekStart: 1
	};

	$(".datepicker").datepicker({
		language: 'es',
		autoclose: true,
		todayHighlight: true,
		format: 'dd/mm/yyyy',
	}).datepicker('update', new Date());
});



function formatDate(timestamp) {
	if (timestamp) {
		var date = new Date(timestamp);
		var day = ('0' + date.getDate()).slice(-2);
		var month = ('0' + (date.getMonth() + 1)).slice(-2);
		var year = date.getFullYear();
		return day + '/' + month + '/' + year;
	}
	return '';
}


//Modal de logout
document.addEventListener("DOMContentLoaded", function() {
	// Manejar el clic en el botón de logout en la barra de navegación
	const element = document.querySelector("a[href='/logout']");
	if (element) {
		element.addEventListener("click", function(event) {
			event.preventDefault(); // Prevenir el logout inmediato
			$('#logoutConfirmModal').modal('show'); // Mostrar el modal de confirmación
		});
	}

	// Ejecutar el logout si se confirma
	document.getElementById("confirmLogoutBtn").addEventListener("click", function() {
		window.location.href = "/logout?logout=true"; // Redirigir al endpoint de logout
	});

});






