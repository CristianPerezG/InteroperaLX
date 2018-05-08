/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function dibujarDataTableLX(nombreTabla) {

    var tablaData = $(nombreTabla).DataTable({
        "dom": '<"top"l>t<"bottom"ipr><"clear">',
        "serverSide": true,
        "processing": true,
        "colReorder": true,
        "order": [[4, "desc"]],
        "ajax": {
            "url": "/InteroperaLX/LXInboxServlet",
            "type": "GET"
        },
        "columns": [
            {
                "data": "IdentidadObjeto",
                "title": "ID Objeto LX",
                "name": "C_NOUN_ID",
                "search": {
                    "regex": true
                }
            },
            {
                "data": "MensajeProceso",
                "title": "Mensaje Proceso",
                "name": "C_MESSAGEID",
                "search": {
                    "regex": true
                }

            },
            {
                "data": "XMLEnviado",
                "title": "XML Enviado",
                "name": "C_XML",
                "search": {
                    "regex": true
                }
            },
            {
                "data": "XMLResultado",
                "title": "XML Resultado",
                "name": "C_REPLY",
                "search": {
                    "regex": true
                }
            },
            {
                "data": "FechaProceso",
                "title": "Fecha Proceso",
                "name": "C_CREATED_DATE_TIME",
                "search": {
                    "regex": true
                }
            },
            {
                "data": "Usuario",
                "title": "Usuario Proceso",
                "name": "C_USER",
                "search": {
                    "regex": true
                }
            },
            {
                "data": "EstadoProceso",
                "title": "Estado Proceso",
                "name": "C_PASS_FAIL",
                "search": {
                    "regex": true
                }
            },
            {
                "data": "NombreFuente",
                "title": "Nombre Fuente",
                "name": "C_SOURCE_NAME",
                "search": {
                    "regex": true
                }
            }
        ],
        "columnDefs": [
            {
                "width": "20%",
                "targets": [0]
            },
            {
                "data": "XMLEnviado",
                "render": function (data, type, row) {
                    return '<button type="button" class="btn btn-success" onclick="mostrarXML(\'' + data + '\')"><i class="fas fa-file-excel"></i></button>';
                },
                "targets": [2]
            },
            {
                "data": "XMLResultado",
                "render": function (data, type, row) {
                    return '<button type="button" class="btn btn-primary" onclick="mostrarXML(\'' + data + '\')"><i class="fas fa-file-excel"></i></button>';
                },
                "targets": [3]
            },
            {
                "data": "EstadoProceso",
                "render": function (data, type, row) {
                    if (data === "Fail") {
                        return '<div class="led-red"></div>';
                    } else {
                        return '<div class="led-green"></div>';
                    }
                },
                "targets": [6]
            },
            {"className": "dt-center", "targets": "_all"}
        ],
        "language": {
            "processing": "Obteniendo Registros...",
            "lengthMenu": "Mostrar _MENU_ registros",
            "zeroRecords": "No se encontraron resultados",
            "emptyTable": "Ningún dato disponible en esta tabla",
            "info": "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
            "infoEmpty": "Mostrando registros del 0 al 0 de un total de 0 registros",
            "infoFiltered": "(filtrado de un total de _MAX_ registros)",
            "infoPostFix": "",
            "search": "Buscar:",
            "url": "",
            "infoThousands": ",",
            "loadingRecords": "Cargando...",
            "paginate": {
                "first": "Primero",
                "last": "Último",
                "next": "Siguiente",
                "previous": "Anterior"
            },
            "aria": {
                "sortAscending": ": Activar para ordenar la columna de manera ascendente",
                "sortDescending": ": Activar para ordenar la columna de manera descendente"
            }
        },
        "searching": true,
        "search": {
            "regex": true
        }
    });

    $("#consultasLX").submit(function (event) {
        event.preventDefault();
        var idObjLX = $('#selectObjeto :selected').val();
        var mensajeProceso = $('#mensajeProceso').val();
        var fechaDesde = $('#fechaDesde').val();
        var fechaHasta = $('#fechaHasta').val();
        var usuarioProceso = $('#usuarioProceso').val();
        var estadoProceso = $('#estadoProceso :selected').val();
        var nombreFuente = $('#nombreFuente').val();

        if (idObjLX !== null && idObjLX !== '' && idObjLX !== ' ' && idObjLX !== 'Seleccione') {
            tablaData.columns(0).search(idObjLX);
        } else {
            tablaData.columns(0).search("");
        }

        if (mensajeProceso !== null && mensajeProceso !== '' && mensajeProceso !== ' ') {
            tablaData.columns(1).search(mensajeProceso);
        } else {
            tablaData.columns(1).search("");
        }

        if (fechaDesde !== null && fechaDesde !== '' && fechaDesde !== ' ') {
            if (fechaHasta !== null && fechaHasta !== '' && fechaHasta !== ' ') {
                tablaData.columns(4).search(fechaDesde + '-sep' + fechaHasta);
            } else {
                tablaData.columns(4).search(fechaDesde + '-sep');
            }
        } else if (fechaHasta !== null && fechaHasta !== '' && fechaHasta !== ' ') {
            tablaData.columns(4).search('-sep' + fechaHasta);
        } else {
            tablaData.columns(4).search("");
        }

        if (usuarioProceso !== null && usuarioProceso !== '' && usuarioProceso !== ' ') {
            tablaData.columns(5).search(usuarioProceso);
        } else {
            tablaData.columns(5).search("");
        }

        if (estadoProceso !== null && estadoProceso !== '' && estadoProceso !== ' ' && estadoProceso !== 'Seleccione') {
            tablaData.columns(6).search(estadoProceso);
        } else {
            tablaData.columns(6).search("");
        }

        if (nombreFuente !== null && nombreFuente !== '' && nombreFuente !== ' ') {
            tablaData.columns(7).search(nombreFuente);
        } else {
            tablaData.columns(7).search("");
        }

        tablaData.draw();
    });

    $("#fechaDesde").datetimepicker({
        firstDay: 1,
        dateFormat: 'dd-mm-yy'
    });

    $("#fechaHasta").datetimepicker({
        firstDay: 1,
        dateFormat: 'dd-mm-yy'
    });
}

function dibujarDataTableWS(nombreTabla) {
    $(nombreTabla).DataTable({
        "dom": '<"top"lf>rt<"bottom"ip><"clear">',
        /*"buttons": ['copy', 'csv', 'excel', 'pdf', 'print'],*/
        "language": {
            "processing": "Obteniendo Registros...",
            "lengthMenu": "Mostrar _MENU_ registros",
            "zeroRecords": "No se encontraron resultados",
            "emptyTable": "Ningún dato disponible en esta tabla",
            "info": "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
            "infoEmpty": "Mostrando registros del 0 al 0 de un total de 0 registros",
            "infoFiltered": "(filtrado de un total de _MAX_ registros)",
            "infoPostFix": "",
            "search": "Buscar:",
            "url": "",
            "infoThousands": ",",
            "loadingRecords": "Cargando...",
            "paginate": {
                "first": "Primero",
                "last": "Último",
                "next": "Siguiente",
                "previous": "Anterior"
            },
            "aria": {
                "sortAscending": ": Activar para ordenar la columna de manera ascendente",
                "sortDescending": ": Activar para ordenar la columna de manera descendente"
            }
        },
        "aoColumnDefs": [{
                "aTargets": [0],
                "bSortable": true,
                "orderData": [0, 1]
            }, ],
        "searching": true,
        "pageLength": 4,
        "destroy": true,
        "stateSave": true,
        "pagingType": "full_numbers"
    });
}

function mostrarXML(urlArchivo) {
    window.open(urlArchivo, '_blank', 'toolbar=yes,scrollbars=yes,resizable=false,top=250,left=1000,width=500,height=500');
}