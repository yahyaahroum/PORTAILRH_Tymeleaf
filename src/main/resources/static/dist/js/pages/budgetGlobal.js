

    $(document).ready(function () {
    var arrayBudget = [];
    $.ajax({
        url: contextPath + '/Budget/All',
        async: true,
        dataType: 'json',
        success: function (data) {
            for (var i = 0, len = data.length; i < len; i++) {
                var id = (data[i].id).toString();
                arrayBudget.push({'value': data[i].nom.trim(), 'data': id});
            }

            loadBudget(arrayBudget);
        }
    });

    function loadBudget(options) {
        $('#budgets').autocomplete({
            lookup: options,
            onSelect: function (suggestion) {
                var id = parseInt(suggestion.data);
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    url: contextPath + '/Budget/' + id,
                    contentType: "application/json;charset-UTF-8",
                    success: function (data) {
                        $('#libelleBudget').val(data.nom);
                        $('#budgetAjoutID').val(data.id);

                    }
                });

            }
        });
    }

});

$(document).ready(function () {
    var bordereauID = parseInt($('#bordereauID').text());
    var budgetAjout = {};
    getAllBudgetGlobal();
    $('#ajoutBudget').click(function () {

        budgetAjout.crunitaire = $('#budgetcrunitaireA').val();

        var budget = $('#budgetAjoutID').val();

        var besoinJSON = JSON.stringify(budgetAjout);
        $.ajax({
            url: contextPath + '/BudgetGobal/Save/'+bordereauID+'/' + budget,
            method: 'POST',
            data: besoinJSON,
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                getAllBudgetGlobal();
                $('#budgetAffaire').text(data.budget.nom);
                $('#exampleModalCenter11').modal('hide');
                $('#exampleModalCenter12').modal('show');

                $('#budgetcrunitaireA').val('');
                $('#budgetAjoutID').val('');
                $('#libelleBudget').val('');
                $('#budgets').val('');
            },
            error: function (error) {
                // Option 1: Afficher un message d'erreur plus spécifique
                alert("Une erreur s'est produite lors de l'opération. Veuillez réessayer.");

                // Option 2: Afficher des détails sur l'erreur dans la console pour le débogage
                console.error("Détails de l'erreur:", error);

                // Option 3: Si vous voulez afficher les détails de l'erreur dans l'alerte
                if (error.responseText) {
                    alert("Erreur: " + error.responseText);
                } else if (error.statusText) {
                    alert("Erreur: " + error.status + " - " + error.statusText);
                } else {
                    alert("Une erreur s'est produite. Veuillez vérifier la console pour plus de détails.");
                }
            }
        })

    })
});

function getAllBudgetGlobal() {
    var bordereauID = parseInt($('#bordereauID').text());
    $.ajax({
        url: contextPath + '/BudgetGobal/GetAllByBordereau/'+bordereauID,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            var tableBody = $('#budget tbody');
            tableBody.empty();
            $(data).each(function (index, element) {

                tableBody.append('<tr><td><div class="form-group">' +
                    '<button class="btn-danger btn-xs" type="button" onclick="deleteBudgetModal(' + element.id + ')"  ><i class="fa fa-trash"></i></button>' +
                    '<button class="btn-info btn-xs"  type="button" onclick="editRowBudget(' + element.id + ')"><i class="fa fa-edit"></i></button>' +

                    '</div></td>' +
                    '<td >' + element.etatBudget + '</td>' +
                    '<td >' + element.budget.codebudget + '</td>' +
                    '<td >' + element.budget.nom + '</td>' +

                    '<td >' + Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(element.crunitaire) + '</td></tr>');
            })
        },
        error: function (error) {
            // Option 1: Afficher un message d'erreur plus spécifique
            alert("Une erreur s'est produite lors de l'opération. Veuillez réessayer.");

            // Option 2: Afficher des détails sur l'erreur dans la console pour le débogage
            console.error("Détails de l'erreur:", error);

            // Option 3: Si vous voulez afficher les détails de l'erreur dans l'alerte
            if (error.responseText) {
                alert("Erreur: " + error.responseText);
            } else if (error.statusText) {
                alert("Erreur: " + error.status + " - " + error.statusText);
            } else {
                alert("Une erreur s'est produite. Veuillez vérifier la console pour plus de détails.");
            }
        }
    });
}

$(document).ready(function () {

    var budget = {};
    getAllBudgetGlobal();
    $('#updateBudget').click(function () {

        var id = parseInt($('#budgetid').text());
        budget.crunitaire = $('#budgetcrunitaire').val();

        var budgetJSON = JSON.stringify(budget);
        $.ajax({
            url: contextPath + '/BudgetGobal/update/' + id,
            method: 'POST',
            data: budgetJSON,
            contentType: "application/json; charset=utf-8",
            success: function () {
                $('#exampleModalCenter4').modal('hide');
                getAllBudgetGlobal();
            },
            error: function (error) {
                // Option 1: Afficher un message d'erreur plus spécifique
                alert("Une erreur s'est produite lors de l'opération. Veuillez réessayer.");

                // Option 2: Afficher des détails sur l'erreur dans la console pour le débogage
                console.error("Détails de l'erreur:", error);

                // Option 3: Si vous voulez afficher les détails de l'erreur dans l'alerte
                if (error.responseText) {
                    alert("Erreur: " + error.responseText);
                } else if (error.statusText) {
                    alert("Erreur: " + error.status + " - " + error.statusText);
                } else {
                    alert("Une erreur s'est produite. Veuillez vérifier la console pour plus de détails.");
                }
            }
        })

    })
});

function editRowBudget(id) {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: contextPath + '/BudgetGobal/get/' + id,
        contentType: "application/json;charset-UTF-8",
        success: function (data) {
            $('#exampleModalCenter4').modal('show');
            $('#budgetid').text(data.id);

            $('#budgetcode').val(data.budget.codebudget);
            $('#budgetname').val(data.budget.nom);

            $('#budgetcrunitaire').val(data.crunitaire);

        }
    });

}

function deleteBudgetModal(id) {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: contextPath + '/BudgetGobal/get/' + id,
        contentType: "application/json;charset-UTF-8",
        success: function (data) {
            $('#debudgetid').text(data.id);
            $('#budgetDesignation').text(data.budget.nom);
            $('#exampleModalCenter5').modal('show');

        }
    });

}

$(document).ready(function () {


    getAllBudgetGlobal();
    $('#deleteBudget').click(function () {
        $('#exampleModalCenter5').modal('hide');
        var id = parseInt($('#debudgetid').text());

        $.ajax({
            url: contextPath + '/BudgetGobal/Delete/' + id,
            method: 'DELETE',
            contentType: "application/json; charset=utf-8",
            success: function () {
                getAllBudgetGlobal();
                $('#exampleModalCenter8').modal('show');

            },
            error: function (error) {
                // Option 1: Afficher un message d'erreur plus spécifique
                alert("Une erreur s'est produite lors de l'opération. Veuillez réessayer.");

                // Option 2: Afficher des détails sur l'erreur dans la console pour le débogage
                console.error("Détails de l'erreur:", error);

                // Option 3: Si vous voulez afficher les détails de l'erreur dans l'alerte
                if (error.responseText) {
                    alert("Erreur: " + error.responseText);
                } else if (error.statusText) {
                    alert("Erreur: " + error.status + " - " + error.statusText);
                } else {
                    alert("Une erreur s'est produite. Veuillez vérifier la console pour plus de détails.");
                }
            }
        })
    })
});

$(document).ready(function () {
    var bordereauID = parseInt($('#bordereauID').text());
    $('#ventilerBudget').click(function () {
        $('#exampleModalCenter3').modal('hide');
        $('#exampleModalCenter7').modal('show');
        $.ajax({
            url: contextPath + '/BudgetGobal/ventiler/'+bordereauID,
            method: 'POST',
            contentType: "application/json; charset=utf-8",
            success: function () {
                $('#exampleModalCenter7').modal('hide');
                getAllBudgetGlobal();
                $('#exampleModalCenter6').modal('show');


            },
            error: function (error) {
                // Option 1: Afficher un message d'erreur plus spécifique
                alert("Une erreur s'est produite lors de l'opération. Veuillez réessayer.");

                // Option 2: Afficher des détails sur l'erreur dans la console pour le débogage
                console.error("Détails de l'erreur:", error);

                // Option 3: Si vous voulez afficher les détails de l'erreur dans l'alerte
                if (error.responseText) {
                    alert("Erreur: " + error.responseText);
                } else if (error.statusText) {
                    alert("Erreur: " + error.status + " - " + error.statusText);
                } else {
                    alert("Une erreur s'est produite. Veuillez vérifier la console pour plus de détails.");
                }
            }
        })

    })
});
