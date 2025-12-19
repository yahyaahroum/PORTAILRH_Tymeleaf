
// Fonction pour formater les nombres avec séparateur de milliers
function formatNumber(num) {
    if (num === undefined || num === null) return "0";
    return parseFloat(num).toLocaleString('fr-FR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

function showRecap() {
    var modal = '<div class="modal fade" id="directRecapModal" tabindex="-1" role="dialog" aria-labelledby="recapModalTitle" aria-hidden="true">' +
    '  <div class="modal-dialog modal-dialog-centered modal-lg" role="document">' +
    '    <div class="modal-content">' +
    '      <div class="modal-header">' +
    '        <h5 class="modal-title" id="recapModalTitle">Récapitulation</h5>' +
    '        <button type="button" class="close" data-dismiss="modal" aria-label="Close">' +
    '          <span aria-hidden="true">&times;</span>' +
    '        </button>' +
    '      </div>' +
    '      <div class="modal-body">' +
    '        <div class="alert alert-info">' +
    '          Les données de récapitulation ne sont pas disponibles actuellement, mais cette fonctionnalité sera bientôt disponible.' +
    '        </div>' +
    '      </div>' +
    '      <div class="modal-footer">' +
    '        <button type="button" class="btn btn-secondary" data-dismiss="modal">Fermer</button>' +
    '      </div>' +
    '    </div>' +
    '  </div>' +
    '</div>';

    if ($('#directRecapModal').length === 0) {
        $('body').append(modal);
    }

    // Afficher la modal
    $('#directRecapModal').modal('show');
}


// Fonction pour afficher le récapitulatif
function afficherRecapitulatif(niveau) {
    // Récupérer l'ID du bordereau depuis l'URL actuelle
    const urlPath = window.location.pathname;
    const bordereauId = urlPath.split('/').pop();
    console.log("Affichage du récapitulatif pour le niveau:", niveau, "bordereau:", bordereauId);

    try {
        $.ajax({
            url: contextPath + `/BordDetail/RecapList/${bordereauId}?niveau=${niveau}`,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                console.log("Données récupérées:", data);

                // Vérifier que les données sont bien reçues
                if (data && Array.isArray(data)) {
                    // Nettoyer la table existante
                    $('.recap-table tbody').empty();

                    // Remplir avec les nouvelles données
                    data.forEach(item => {
                        const row = `<tr>
                            <td>${item.designation || ''}</td>
                            <td>${formatNumber(item.coutRevient)} MAD</td>
                            <td>${formatNumber(item.marge)} %</td>
                            <td>${formatNumber(item.mntMarge)} MAD</td>
                            <td>${formatNumber(item.sumPrixHT)} MAD</td>
                            <td>${formatNumber(item.rabais)} %</td>
                            <td>${formatNumber(item.mntAfterrabais)} MAD</td>
                        </tr>`;
                        $('.recap-table tbody').append(row);
                    });
                } else {
                    console.error("Format de données inattendu:", data);
                    $('.recap-table tbody').html("<tr><td colspan='7'>Aucune donnée disponible</td></tr>");
                }
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors du chargement des données de récapitulation:", error);
                console.error("URL utilisée:", contextPath + `/BordDetail/RecapList/${bordereauId}?niveau=${niveau}`);
                $('.recap-table tbody').html("<tr><td colspan='7'>Erreur de chargement des données</td></tr>");
            }
        });
    } catch (e) {
        console.error("Exception lors de l'appel AJAX:", e);
    }
}

// Fonction pour corriger l'objet reçu dans la méthode de récapitulation
function corrigerObjetRecap(objet) {
    // Si l'objet est une chaîne JSON, la parser
    if (typeof objet === 'string') {
        try {
            return JSON.parse(objet);
        } catch (e) {
            console.error("Erreur lors de l'analyse de la chaîne JSON:", e);
            return objet;
        }
    }

    return objet;
}

function afficherMessageRecap(data) {
    // S'assurer que data est un objet et non une chaîne "[object Object]"
    try {
        if (typeof data === 'string') {
            try {
                data = JSON.parse(data);
            } catch (e) {
                console.warn("Le message n'est pas au format JSON:", data);
            }
        }

        if (typeof data === 'object' && data !== null) {
            $('#lot').text(data.designation || '');
            $('#coutRevient').text(formatNumber(data.coutRevient) + ' MAD');
            $('#marge').text(formatNumber(data.marge) + ' %');
            $('#montantMarge').text(formatNumber(data.mntMarge) + ' MAD');
            $('#prixVente').text(formatNumber(data.sumPrixHT) + ' MAD');
            $('#rabais').text(formatNumber(data.rabais) + ' %');
            $('#montantRabais').text(formatNumber(data.mntAfterrabais) + ' MAD');

            $('#recapModal').modal('show');
        } else {
            console.error("Les données de récapitulation ne sont pas au format attendu:", data);
            alert("Erreur lors de l'affichage des données de récapitulation.");
        }
    } catch (e) {
        console.error("Exception lors de l'affichage du récapitulatif:", e);
        alert("Une erreur s'est produite lors de l'affichage du récapitulatif.");
    }
}

function calculateSaisi() {
    var quantity = Number(document.getElementById('quantite').value);
    var chute = Number(document.getElementById('chute').value);
    var priceSaisi = document.getElementById('coutRevSais').value;
    var price = document.getElementById('coutRev').value;
    var totalSaisi = document.getElementById('coutRevientSaisi');
    var totalRev = document.getElementById('coutRevTotal');
    var quantityChute = quantity * chute / 100;
    if (chute == '' || chute == 0) {
        var resultSaisi = quantity * priceSaisi;
        var result = quantity * price;
    } else if (chute != '' || chute != 0) {
        var resultSaisi = (quantityChute + quantity) * priceSaisi;
        var result = (quantityChute + quantity) * price;
    }

    totalSaisi.value = resultSaisi;
    totalRev.value = result;
}

function calculateEditBesoin() {
    var quantity = Number(document.getElementById('besoinquantite').value);
    var chute = Number(document.getElementById('chuteEdit').value);
    var priceSaisi = document.getElementById('besoincrs').value;
    var price = document.getElementById('besoincrunitaire').value;
    var totalSaisi = document.getElementById('besoincrsTot');
    var totalRev = document.getElementById('besoinTotCR');
    var quantityChute = quantity * chute / 100;

    if (chute == '' || chute == 0) {
        var resultSaisi = quantity * priceSaisi;
        var result = quantity * price;
    } else if (chute != '' || chute != 0) {
        var resultSaisi = (quantityChute + quantity) * priceSaisi;
        var result = (quantityChute + quantity) * price;
    }

    totalSaisi.value = resultSaisi;
    totalRev.value = result;
}

function calculateMO() {
    var quantity = document.getElementById('qMO').value;
    var coutRev = document.getElementById('coutRevFonction').value;
    var total = document.getElementById('coutRevTotalFonction');
    var result = quantity * coutRev;
    total.value = result;
}

function convertToDecimal() {
    const heures = parseInt(document.getElementById('heures').value) || 0;
    const minutes = parseInt(document.getElementById('minutes').value) || 0;

    // Conversion en décimal (2h30 = 2.5)
    const decimalValue = heures + (minutes / 60);

    console.log("Conversion: " + heures + "h" + minutes + "min -> " + decimalValue.toFixed(2));

    // Mettre à jour le champ caché
    document.getElementById('qMOEdit').value = decimalValue.toFixed(2);

    // Déclencher le calcul du total
    calculateEditActivite();
}

// Fonction pour convertir une valeur décimale en heures et minutes
function displayTimeValue(decimalValue) {
    if (!decimalValue) decimalValue = 0;

    // Extraire les heures (partie entière)
    const heures = Math.floor(decimalValue);

    // Calculer les minutes (partie décimale convertie en minutes)
    const minutes = Math.round((decimalValue - heures) * 60);

    // Définir les valeurs dans les champs
    document.getElementById('heures').value = heures;
    document.getElementById('minutes').value = minutes;

    // S'assurer que la valeur décimale est stockée dans le champ caché
    document.getElementById('qMOEdit').value = decimalValue;
}

function calculateEditActivite() {
    var quantity = document.getElementById('qMOEdit').value;
    var coutRev = document.getElementById('cRevientActivite').value;
    var total = document.getElementById('TotActivite');
    var result = quantity * coutRev;

    total.value = result;
}

$(document).ready(function () {
    var lineId = parseInt($('#lineID').text());

    // Initialisation pour la récapitulation
    $("#selectionner-niveau").on('change', function() {
        const niveau = $(this).val();
        if (niveau) {
            afficherRecapitulatif(niveau);
        }
    });

    // Gestionnaire pour le bouton Recap
    $(document).on('click', '.Recap', function() {
        // Charger les niveaux disponibles directement
        $.ajax({
            url: contextPath + `/BordDetail/NiveauList/${lineId}`,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                console.log("Niveaux chargés:", data);
                const niveauSelect = $('#selectionner-niveau');
                niveauSelect.empty();

                niveauSelect.append('<option value="">Sélectionner un niveau</option>');

                // Ajouter les niveaux disponibles
                if (data && Array.isArray(data)) {
                    data.forEach(niveau => {
                        niveauSelect.append(`<option value="${niveau}">Niveau ${niveau}</option>`);
                    });
                }

                // Afficher la modal
                $('#recapModal').modal('show');
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors du chargement des niveaux:", error);
                console.error("Status:", xhr.status);
                console.error("Response text:", xhr.responseText);
                alert("Impossible de charger les niveaux. Veuillez vérifier la console pour plus de détails.");
            }
        });
    });

    var arrayFunction = [];
    $.ajax({
        url: contextPath + "/MO/getAllFunction",
        async: true,
        dataType: 'json',
        success: function (data) {
            for (var i = 0, len = data.length; i < len; i++) {
                var id = (data[i].id).toString();
                arrayFunction.push({ 'value': data[i].libelle.trim(), 'data': id });
            }

            loadFunction(arrayFunction);
        }
    });

    function loadFunction(options) {
        $('#fonctions').autocomplete({
            lookup: options,
            onSelect: function (suggestion) {
                var id = parseInt(suggestion.data);
                $.ajax({
                    type: "GET",
                    dataType: "json",
                    url: contextPath + "/MO/getOneFunction/" + id,
                    contentType: "application/json;charset-UTF-8",
                    success: function (data) {
                        $('#fonction').val(data.id);
                    }
                });
            }
        });
    }
});

$(document).ready(function () {
    var lineId = parseInt($('#lineID').text());
    var AjoutMO = {};
    getAllBesoin();
    getAllBudget();
    getAllActivity();

    $('#ajoutMO').click(function () {
        if ($('#qMO').val() == '' || $('#optionActivite').val() == '' || $('#qMO').val() == 0 || $('#coutRevFonction').val() == '' || $('#coutRevFonction').val() == 0) {
            alert('Vous devez vérifier les champs !!');
        } else if ($('#qMO').val() != '' && $('#optionActivite').val() != '' && $('#qMO').val() != 0 && $('#coutRevFonction').val() != '' && $('#coutRevFonction').val() != 0) {
            AjoutMO.quantite = $('#qMO').val();
            AjoutMO.coutRev = $('#coutRevFonction').val();
            AjoutMO.coutRevTotal = $('#coutRevTotalFonction').val();
            AjoutMO.activityOption = $('#optionActivite').val();

            var fonction = $('#fonction').val();

            var moJSON = JSON.stringify(AjoutMO);
            $.ajax({
                url: contextPath + '/MO/Ajouter/' + lineId + '?fonction=' + fonction,
                method: 'POST',
                data: moJSON,
                contentType: "application/json; charset=utf-8",
                success: function (data) {
                    $('#qMO').val(0);
                    $('#coutRevFonction').val(0);
                    $('#coutRevTotalFonction').val(0);
                    $('#fonctions').val('');
                    getAllBesoin();
                    getAllBudget();
                    getAllActivity();
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
    });
});

function updateTotalPrixHT() {
    let total = 0;
    $('#recapTable tbody tr').each(function() {
        let prixHT = parseFloat($(this).find('td').eq(4).text()) || 0; // 4e colonne (Prix HT)
        total += prixHT;
    });
    $('#totalPrixHT').text(total.toFixed(2) + ' MAD');
}

function getAllActivity() {
    var lineId = parseInt($('#lineID').text());
    $.ajax({
        url: contextPath + '/MO/getAllActivity/' + lineId,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            var tableBody = $('#activity tbody');
            tableBody.empty();
            $(data).each(function (index, element) {
                tableBody.append('<tr><td><div class="form-group">' +
                    '<button class="btn-danger btn-xs" type="button" onclick="deleteActiviteModal(' + element.id + ')"  ><i class="fa fa-trash"></i></button>' +
                    '<button class="btn-info btn-xs" type="button" onClick="editRowActivite(' + element.id + ')"><i class="fa fa-edit"></i></button></div></td>' +
                    '<td>' + element.fonction.libelle + '</td>' +
                    '<td>' + element.unite + '</td>' +
                    '<td>' + element.quantite + '</td>' +
                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRev) + '</td>' +
                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRevTotal) + '</td>' +
                    '<td>' + element.budget.codebudget + '</td>' +
                    '<td>' + element.activityOption + '</td>' +
                    '</tr>');
            });
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
    var lineId = parseInt($('#lineID').text());
    $('#genererBudgetMO').click(function () {
        $('#exampleModalCenter131').modal('hide');
        $.ajax({
            url: contextPath + '/MO/generer/' + lineId,
            method: 'POST',
            contentType: "application/json; charset=utf-8",
            success: function () {
                getAllBudget();
                getAllBesoin();
                getAllActivity();
                window.location.reload();
                $('#exampleModalCenter61').modal('show');
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
    });
});

function deleteActiviteModal(id) {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: contextPath + "/MO/Get/" + id,
        contentType: "application/json;charset-UTF-8",
        success: function (data) {
            $('#deactiviteid').text(data.id);
            $('#activiteDesignation').text(data.fonction.libelle.trim());
            $('#exampleModalCenter91').modal('show');
        }
    });
}

$(document).ready(function () {
    getAllBesoin();
    getAllBudget();
    getAllActivity();
    $('#deleteActivite').click(function () {
        var id = parseInt($('#deactiviteid').text());
        $.ajax({
            url: contextPath + '/MO/Delete/' + id,
            method: 'DELETE',
            contentType: "application/json; charset=utf-8",
            success: function () {
                $('#exampleModalCenter91').modal('hide');
                getAllBesoin();
                getAllBudget();
                getAllActivity();
                $('#exampleModalCenter81').modal('show');
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
    });
});

function editRowActivite(id) {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: contextPath + '/MO/Get/' + id,
        contentType: "application/json;charset-UTF-8",
        success: function (data) {
            $('#exampleModalCenter141').modal('show');
            $('#ActiviteId').text(data.id);
            $('#fonctionActivite').val(data.fonction.libelle.trim());
            $('#qMOEdit').val(data.quantite);
            displayTimeValue(data.quantite);
            $('#cRevientActivite').val(data.coutRev);
            $('#TotActivite').val(data.coutRevTotal);
            $('#optionActiviteUpdate').val(data.activityOption).change();
        }
    });
}

$(document).ready(function () {
    var activite = {};
    getAllBesoin();
    getAllBudget();
    getAllActivity();
    $('#updateActivite').click(function () {
        var id = parseInt($('#ActiviteId').text());
        activite.quantite = $('#qMOEdit').val();
        activite.coutRev = $('#cRevientActivite').val();
        activite.coutRevTotal = $('#TotActivite').val();
        activite.activityOption = $('#optionActiviteUpdate').val();

        var ActiviteJSON = JSON.stringify(activite);
        $.ajax({
            url: contextPath + '/MO/update/' + id,
            method: 'POST',
            data: ActiviteJSON,
            contentType: "application/json; charset=utf-8",
            success: function () {
                $('#exampleModalCenter141').modal('hide');
                getAllBesoin();
                getAllBudget();
                getAllActivity();
                $('#exampleModalCenter81').modal('show');
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
    });
});

function editRowBudget(id) {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: contextPath + '/BudgetAffaire/' + id,
        contentType: "application/json;charset-UTF-8",
        success: function (data) {
            $('#exampleModalCenter4').modal('show');
            $('#budgetid').text(data.id);
            $('#budgetcode').val(data.budget.codebudget);
            $('#budgetname').val(data.budget.nom);
            $('#budgetquantite').val(data.quantite);
            $('#budgetcrunitaire').val(Number.parseFloat(data.crunitaire).toFixed(2));
            $('#budgetmarge').val(Number.parseFloat(data.marge).toFixed(0));
            $('#budgetTotCR').val(Number.parseFloat(data.totalCoutRe).toFixed(2));
            $('#montantMargeE').val(Number.parseFloat((data.marge * data.totalCoutRe) / 100).toFixed(2));
            $('#CRtotMargeE').val(Number.parseFloat(data.crmarge).toFixed(2));
        }
    });
}

function editRowBesoin(id) {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: contextPath + "/Besoin/" + id,
        contentType: "application/json;charset-UTF-8",
        success: function (data) {
            $('#exampleModalCenter14').modal('show');
            $('#besoinId').text(data.id);
            $('#besoinbudgetcode').val(data.budget.nom);
            $('#besoinArticleEdit').val(data.designationBes);
            $('#besoinquantite').val(Number.parseFloat(data.quantite).toFixed(2));
            $('#besoincrunitaire').val(Number.parseFloat(data.coutRev).toFixed(2));
            $('#besoinTotCR').val(Number.parseFloat(data.coutRevTotal).toFixed(2));
            $('#besoincrs').val(Number.parseFloat(data.coutRevSaisi).toFixed(2));
            $('#besoincrsTot').val(Number.parseFloat(data.coutRevSaisiTotal).toFixed(2));
            $('#chuteEdit').val(data.chute);
            $('#longueurEdit').val(data.longueur);
            $('#largeurEdit').val(data.largeur);
            $('#hauteurEgit').val(data.hauteur);
            $('#multiplicationEdit').val(data.multiplication);
        },
        error: function (error) {
            alert("Droit d'accés refusés");
        }
    });
}

function deleteBudgetModal(id) {
    $.ajax({
        type: "GET",
        dataType: "json",
        url: contextPath + '/BudgetAffaire/' + id,
        contentType: "application/json;charset-UTF-8",
        success: function (data) {
            $('#debudgetid').text(data.id);
            $('#budgetDesignation').text(data.budget.nom);
            $('#exampleModalCenter5').modal('show');
        }
    });
}

function totCRBudget() {
    var quantiteAjout = document.getElementById('budgetquantiteA').value;
    var quantiteEdit = document.getElementById('budgetquantite').value;
    var margeAjout = document.getElementById('budgetmargeA').value;
    var marge = document.getElementById('budgetmarge').value;
    var crunitaireAjout = document.getElementById('budgetcrunitaireA').value;
    var crunitaireEdit = document.getElementById('budgetcrunitaire').value;
    var TotCRBudget = document.getElementById('budgetTotCR');
    var TotCRBudgetAjout = document.getElementById('budgetTotCRA');
    var TotCRBudgetMargeAjout = document.getElementById('CRtotMarge');
    var TotCRBudgetMargeEdit = document.getElementById('CRtotMargeE');
    var MontantMargeAjout = document.getElementById('montantMarge');
    var MontantMargeEdit = document.getElementById('montantMargeE');

    var resultAjout = quantiteAjout * crunitaireAjout;
    var resultMarge = ((marge * (crunitaireEdit * quantiteEdit)) / 100);
    var resulToTtMarge = ((marge * (crunitaireEdit * quantiteEdit)) / 100) + (crunitaireEdit * quantiteEdit);
    var resulMargetAjout = (margeAjout * (quantiteAjout * crunitaireAjout)) / 100;
    var resulTotMargetAjout = ((margeAjout * (quantiteAjout * crunitaireAjout)) / 100) + (quantiteAjout * crunitaireAjout);

    TotCRBudget.value = crunitaireEdit * quantiteEdit;
    TotCRBudgetAjout.value = resultAjout;
    MontantMargeAjout.value = resulMargetAjout;
    MontantMargeEdit.value = resultMarge;
    TotCRBudgetMargeAjout.value = resulTotMargetAjout;
    TotCRBudgetMargeEdit.value = resulToTtMarge;
}

$(document).ready(function () {
    getAllBesoin();
    getAllBudget();
    getAllActivity();
    $('#deleteBudget').click(function () {
        var id = parseInt($('#debudgetid').text());
        $.ajax({
            url: contextPath + '/Budget/supprimer/' + id,
            method: 'DELETE',
            contentType: "application/json; charset=utf-8",
            success: function () {
                $('#exampleModalCenter5').modal('hide');
                getAllBesoin();
                getAllBudget();
                getAllActivity();
                $('#exampleModalCenter8').modal('show');
            },
            error: function (error) {
                alert("Droits d'accés refusés");
            }
        });
    });
});

$(document).ready(function () {
    var budget = {};
    getAllBesoin();
    getAllBudget();
    getAllActivity();
    $('#updateBudget').click(function () {
        var id = parseInt($('#budgetid').text());
        budget.quantite = $('#budgetquantite').val();
        budget.crunitaire = $('#budgetcrunitaire').val();
        budget.totalCoutRe = $('#budgetTotCR').val();
        budget.marge = $('#budgetmarge').val();
        budget.crmarge = $('#CRtotMargeE').val();

        var budgetJSON = JSON.stringify(budget);
        $.ajax({
            url: contextPath + '/Budget/update/' + id,
            method: 'POST',
            data: budgetJSON,
            contentType: "application/json; charset=utf-8",
            success: function () {
                $('#exampleModalCenter4').modal('hide');
                getAllBesoin();
                getAllBudget();
                getAllActivity();
            },
            error: function (error) {
                alert("Droit d'accés refusés");
            }
        });
    });
});

$(document).ready(function () {
    var besoin = {};
    getAllBesoin();
    getAllBudget();
    getAllActivity();
    $('#updatebesoin').click(function () {
        var id = parseInt($('#besoinId').text());
        besoin.quantite = $('#besoinquantite').val();
        besoin.coutRev = $('#besoincrunitaire').val();
        besoin.coutRevSaisi = $('#besoincrs').val();
        besoin.coutRevTotal = $('#besoinTotCR').val();
        besoin.coutRevSaisiTotal = $('#besoincrsTot').val();
        besoin.chute = $('#chuteEdit').val();
        besoin.longueur = $('#longueurEdit').val();
        besoin.largeur = $('#largeurEdit').val();
        besoin.hauteur = $('#hauteurEgit').val();
        besoin.multiplication = $('#multiplicationEdit').val();

        var besoinJSON = JSON.stringify(besoin);
        $.ajax({
            url: contextPath + '/Besoin/update/' + id,
            method: 'POST',
            data: besoinJSON,
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                $('#exampleModalCenter14').modal('hide');
                getAllBesoin();
                getAllBudget();
                getAllActivity();
                $('#besoinArticleEditSuccess').text(data.designationBes);
                $('#exampleModalCenter3').modal('show');
            },
            error: function (error) {
                alert("Droit d'accés refusés");
            }
        });
    });
});

$(document).ready(function () {
    var budgetAjout = {};
    getAllBesoin();
    getAllBudget();
    getAllActivity();
    $('#ajoutBudget').click(function () {
        var lineId = parseInt($('#lineID').text());
        budgetAjout.quantite = $('#budgetquantiteA').val();
        budgetAjout.crunitaire = $('#budgetcrunitaireA').val();
        budgetAjout.totalCoutRe = $('#budgetTotCRA').val();
        budgetAjout.marge = $('#budgetmargeA').val();
        budgetAjout.coutRev = $('#budgetTotCRA').val();
        budgetAjout.crmarge = $('#CRtotMarge').val();

        var budget = $('#budgetAjoutID').val();

        var budgetJSON = JSON.stringify(budgetAjout);
        $.ajax({
            url: contextPath + '/BudgetAffaire/Ajouter/' + lineId + '/' + budget,
            method: 'POST',
            data: budgetJSON,
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                getAllBesoin();
                getAllBudget();
                getAllActivity();
                $('#budgetAffaire').text(data.budget.nom);
                $('#exampleModalCenter11').modal('hide');
                $('#exampleModalCenter12').modal('show');
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
    });
});

$(document).ready(function () {
    var lineId = parseInt($('#lineID').text());
    var besoin = {};
    getAllBesoin();
    getAllBudget();
    getAllActivity();
    $('#addBesoin').click(function () {
        if ($('#quantite').val() == '' || $('#quantite').val() == 0 || $('#coutRevSais').val() == 0 || $('#coutRevSais').val() == '') {
            alert('Vous devez vérifier les champs !!');
        } else if ($('#quantite').val() != '' && $('#quantite').val() != 0 && $('#coutRevSais').val() != 0 && $('#coutRevSais').val() != '') {
            var quantity = Number($('#quantite').val());
            var chute = Number($('#chute').val());
            if (chute == '' || chute == 0) {
                besoin.quantite = quantity;
                besoin.chute = 0;
            } else if (chute != '' || chute != 0) {
                besoin.chute = chute;
                besoin.quantite = quantity;
            }

            besoin.designationBes = $('#valueArticle').val();
            besoin.uniteRef = $('#unite').val();
            besoin.coutRev = $('#coutRev').val();
            besoin.coutRevSaisi = $('#coutRevSais').val();
            besoin.coutRevTotal = $('#coutRevTotal').val();
            besoin.coutRevSaisiTotal = $('#coutRevientSaisi').val();
            besoin.longueur = $('#Longueur').val();
            besoin.largeur = $('#Largeur').val();
            besoin.hauteur = $('#Hauteur').val();
            besoin.multiplication = $('#dimQuantite').val();

            var article = $('#articleId').val();
            var fournisseur = $('#fournisseurID').val() == null ? null : $('#fournisseurID').val();

            var besoinJSON = JSON.stringify(besoin);
            $.ajax({
                url: contextPath + '/Besoin/Ajouter/' + lineId + '/' + article + '?fournisseur=' + fournisseur,
                method: 'POST',
                data: besoinJSON,
                contentType: "application/json; charset=utf-8",
                                success: function (data) {
                                    getAllBesoin();
                                    getAllBudget();
                                    getAllActivity();
                                    $('#besoinArticle').text(data.designationBes);
                                    $('#exampleModalCenter7').modal('show');

                                    $('#articl').val('');
                                    $('#valueArticle').val('');
                                    $('#unite').val('');
                                    $('#quantite').val(0);
                                    $('#chute').val(0);
                                    $('#coutRev').val(0);
                                    $('#coutRevSais').val(0);
                                    $('#coutRevTotal').val(0);
                                    $('#coutRevientSaisi').val(0);
                                    $('#Longueur').val(1);
                                    $('#Largeur').val(1);
                                    $('#Hauteur').val(1);
                                    $('#dimQuantite').val(1);
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
                    });
                });

                function getAllBesoin() {
                    var lineId = parseInt($('#lineID').text());
                    console.log($('#lineID').text());
                    $.ajax({
                        url: contextPath + '/Besoin/Tous/' + lineId,
                        method: 'GET',
                        dataType: 'json',
                        success: function (data) {
                            var tableBody = $('#besoin tbody');
                            tableBody.empty();
                            $(data).each(function (index, element) {
                                tableBody.append('<tr><td><div class="form-group">' +
                                    '<button class="btn-danger btn-xs" type="button" onclick="deleteBesoinModal(' + element.id + ')"  ><i class="fa fa-trash"></i></button>' +
                                    '<button class="btn-info btn-xs" type="button" onClick="editRowBesoin(' + element.id + ')"><i class="fa fa-edit"></i></button></div></td>' +
                                    '<td>' + element.referenceBes + '</td>' +
                                    '<td>' + element.designationBes + '</td>' +
                                    '<td>' + element.quantite + '</td>' +
                                    '<td>' + element.chute + '</td>' +
                                    '<td>' + element.longueur + '</td>' +
                                    '<td>' + element.largeur + '</td>' +
                                    '<td>' + element.hauteur + '</td>' +
                                    '<td>' + element.uniteRef + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRev) + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRevTotal) + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRevSaisi) + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRevSaisiTotal) + '</td>' +
                                    '<td>' + element.budget.codebudget + '</td>' +
                                    '</tr>');
                            });
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

                function getAllBudget() {
                    var lineId = parseInt($('#lineID').text());
                    $.ajax({
                        url: contextPath + '/Budget/Tous/' + lineId,
                        method: 'GET',
                        dataType: 'json',
                        success: function (data) {
                            var tableBody = $('#budget tbody');
                            tableBody.empty();
                            $(data).each(function (index, element) {
                                if (element.etatBudget == 'Budget_Saisi') {
                                    tableBody.append('<tr><td><div class="form-group">' +
                                        '<button class="btn-danger btn-xs" type="button" onclick="deleteBudgetModal(' + element.id + ')"  ><i class="fa fa-trash"></i></button>' +
                                        '<button class="btn-info btn-xs"  type="button" onclick="editRowBudget(' + element.id + ')"><i class="fa fa-edit"></i></button>' +
                                        '</div></td>' +
                                        '<td >' + element.etatBudget + '</td>' +
                                        '<td >' + element.budget.codebudget + '</td>' +
                                        '<td >' + element.budget.nom + '</td>' +
                                        '<td >' + element.quantite + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.crunitaire) + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.totalCoutRe) + '</td>' +
                                        '<td >' + Number.parseFloat(element.marge).toFixed(0) + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.crmarge) + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRev) + '</td>' +
                                        '<td >' + element.frais + '</td>' +
                                        '<td >' + element.uniteVente + '</td>');
                                }
                                if (element.etatBudget == 'Budget_Généré') {
                                    tableBody.append('<tr><td><div class="form-group">' +
                                        '<button class="btn-default btn-xs"  type="button" onclick="editRowBudget(' + element.id + ')"><i class="fa fa-edit"></i></button>' +
                                        '</div></td>' +
                                        '<td >' + element.etatBudget + '</td>' +
                                        '<td >' + element.budget.codebudget + '</td>' +
                                        '<td >' + element.budget.nom + '</td>' +
                                        '<td >' + element.quantite + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.crunitaire) + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.totalCoutRe) + '</td>' +
                                        '<td >' + Number.parseFloat(element.marge).toFixed(0) + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.crmarge) + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRev) + '</td>' +
                                        '<td >' + element.frais + '</td>' +
                                        '<td >' + element.uniteVente + '</td>');
                                }
                                if (element.etatBudget == 'Budget_Global') {
                                    tableBody.append('<tr><td><div class="form-group">' +
                                        '<button class="btn-default btn-xs"  type="button" onclick="editRowBudget(' + element.id + ')"><i class="fa fa-edit"></i></button>' +
                                        '</div></td>' +
                                        '<td >' + element.etatBudget + '</td>' +
                                        '<td >' + element.budget.codebudget + '</td>' +
                                        '<td >' + element.budget.nom + '</td>' +
                                        '<td >' + element.quantite + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.crunitaire) + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.totalCoutRe) + '</td>' +
                                        '<td >' + Number.parseFloat(element.marge).toFixed(0) + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.crmarge) + '</td>' +
                                        '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRev) + '</td>' +
                                        '<td >' + element.frais + '</td>' +
                                        '<td >' + element.uniteVente + '</td>');
                                }
                            });
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
                    var lineId = parseInt($('#lineID').text());
                    $('#genererBudget').click(function () {
                        $('#exampleModalCenter13').modal('hide');
                        $.ajax({
                            url: contextPath + '/Budget/Generer/' + lineId,
                            method: 'POST',
                            contentType: "application/json; charset=utf-8",
                            success: function () {
                                getAllBesoin();
                                getAllBudget();
                                getAllActivity();
                                window.location.reload();
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
                        });
                    });
                });

                function deleteBesoinModal(id) {
                    $.ajax({
                        type: "GET",
                        dataType: "json",
                        url: contextPath + '/Besoin/' + id,
                        contentType: "application/json;charset-UTF-8",
                        success: function (data) {
                            $('#debesoinid').text(data.id);
                            $('#besoinDesignation').text(data.designationBes);
                            $('#exampleModalCenter9').modal('show');
                        }
                    });
                }

                $(document).ready(function () {
                    getAllBesoin();
                    getAllBudget();
                    getAllActivity();
                    $('#deleteBesoin').click(function () {
                        var id = parseInt($('#debesoinid').text());
                        $.ajax({
                            url: contextPath + '/Besoin/supprimer/' + id,
                            method: 'DELETE',
                            contentType: "application/json; charset=utf-8",
                            success: function () {
                                $('#exampleModalCenter9').modal('hide');
                                getAllBesoin();
                                getAllBudget();
                                getAllActivity();
                                $('#exampleModalCenter10').modal('show');
                            },
                            error: function (error) {
                                alert("Droits d'accés refusés");
                            }
                        });
                    });
                });

                $(document).ready(function () {
                    var arrayArticle = [];
                    $.ajax({
                        url: contextPath + "/Article/ALL",
                        async: true,
                        dataType: 'json',
                        success: function (data) {
                            for (var i = 0, len = data.length; i < len; i++) {
                                var id = (data[i].id).toString();
                                arrayArticle.push({ 'value': data[i].designationArticle.trim(), 'data': id });
                            }
                            loadArticle(arrayArticle);
                        }
                    });

                    function loadArticle(options) {
                        $('#articl').autocomplete({
                            lookup: options,
                            onSelect: function (suggestion) {
                                var id = parseInt(suggestion.data);
                                $.ajax({
                                    type: "GET",
                                    dataType: "json",
                                    url: contextPath + '/Article/' + id,
                                    contentType: "application/json;charset-UTF-8",
                                    success: function (data) {
                                        $('#articleId').val(data.id);
                                        $('#valueArticle').val(data.designationArticle.trim());
                                        $('#unite').val(data.uniteRefArticle.trim());
                                        $('#coutRev').val(data.coutArticle);
                                    }
                                });
                            }
                        });
                    }
                });

                $(document).ready(function () {
                    var arrayBudget = [];
                    $.ajax({
                        url: contextPath + "/Budget/All",
                        async: true,
                        dataType: 'json',
                        success: function (data) {
                            for (var i = 0, len = data.length; i < len; i++) {
                                var id = (data[i].id).toString();
                                arrayBudget.push({ 'value': data[i].nom.trim(), 'data': id });
                            }
                            loadBudget(arrayBudget);
                            loadBudgetArticle(arrayBudget);
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
                                    url: contextPath + "/Budget/" + id,
                                    contentType: "application/json;charset-UTF-8",
                                    success: function (data) {
                                        $('#libelleBudget').val(data.nom);
                                        $('#budgetAjoutID').val(data.id);
                                    }
                                });
                            }
                        });
                    }

                    function loadBudgetArticle(options) {
                        $('#budgetArticle').autocomplete({
                            lookup: options,
                            onSelect: function (suggestion) {
                                var id = parseInt(suggestion.data);
                                $.ajax({
                                    type: "GET",
                                    dataType: "json",
                                    url: contextPath + "/Budget/" + id,
                                    contentType: "application/json;charset-UTF-8",
                                    success: function (data) {
                                        $('#budgetIdArticle').val(data.id);
                                    }
                                });
                            }
                        });
                    }
                });

                $(document).ready(function () {
                    var articles = {};
                    getAllBesoin();
                    getAllBudget();
                    getAllActivity();
                    $('#ajouterArticle').click(function () {
                        articles.referenceArticle = null;
                        articles.designationArticle = $('#articleDesignation').val();
                        articles.uniteRefArticle = $('#uniteArticle').val();
                        articles.coutArticle = $('#coutArticle').val();

                        var articleJSON = JSON.stringify(articles);
                        var id = $('#budgetIdArticle').val();
                        $.ajax({
                            url: contextPath + '/Article/Ajouter/' + id,
                            method: 'POST',
                            data: articleJSON,
                            contentType: "application/json; charset=utf-8",
                            success: function (data) {
                                getAllBesoin();
                                getAllBudget();
                                getAllActivity();
                                $('#articleSuccess').text(data.designationArticle);
                                $('#exampleModalCenter1').modal('hide');
                                $('#exampleModalCenter15').modal('show');
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
                    });
                });

                $(document).ready(function () {
                    var fournisseurs = {};
                    getAllBesoin();
                    getAllBudget();
                    getAllActivity();
                    $('#ajouterFournisseur').click(function () {
                        fournisseurs.code = null;
                        fournisseurs.libelle = $('#nomFournisseur').val();

                        var FournisseurJSON = JSON.stringify(fournisseurs);
                        var id = $('#deviseIdFournisseur').val();
                        $.ajax({
                            url: contextPath + '/Fournisseur/Ajouter/' + id,
                            method: 'POST',
                            data: FournisseurJSON,
                            contentType: "application/json; charset=utf-8",
                            success: function (data) {
                                getAllBesoin();
                                getAllBudget();
                                getAllActivity();
                                $('#fournisseurSuccess').text(data.libelle);
                                $('#exampleModalCenter').modal('hide');
                                $('#exampleModalCenter16').modal('show');
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
                    });
                });

                $(document).ready(function () {
                    var arrayFournisseur = [];
                    $.ajax({
                        url: contextPath + "/Fournisseur/All",
                        async: true,
                        dataType: 'json',
                        success: function (data) {
                            for (var i = 0, len = data.length; i < len; i++) {
                                var id = (data[i].id).toString();
                                arrayFournisseur.push({ 'value': data[i].libelle.trim(), 'data': id });
                            }
                            loadFournisseur(arrayFournisseur);
                        }
                    });

                    function loadFournisseur(options) {
                        $('#fournisseur').autocomplete({
                            lookup: options,
                            onSelect: function (suggestion) {
                                var id = parseInt(suggestion.data);
                                $.ajax({
                                    type: "GET",
                                    dataType: "json",
                                    url: contextPath + "/Fournisseur/" + id,
                                    contentType: "application/json;charset-UTF-8",
                                    success: function (data) {
                                        $('#fournisseurID').val(data.id);
                                    }
                                });
                            }
                        });
                    }
                });

                $(document).ready(function () {
                    var arrayDevise = [];
                    $.ajax({
                        url: contextPath + "/Devise/All",
                        async: true,
                        dataType: 'json',
                        success: function (data) {
                            for (var i = 0, len = data.length; i < len; i++) {
                                var id = (data[i].id).toString();
                                arrayDevise.push({ 'value': data[i].code.trim(), 'data': id });
                            }
                            loadDeviseFournisseur(arrayDevise);
                            loadDevise(arrayDevise);
                        }
                    });

                    function loadDeviseFournisseur(options) {
                        $('#deviseFournisseur').autocomplete({
                            lookup: options,
                            onSelect: function (suggestion) {
                                var id = parseInt(suggestion.data);
                                $.ajax({
                                    type: "GET",
                                    dataType: "json",
                                    url: contextPath + "/Devise/get/" + id,
                                    contentType: "application/json;charset-UTF-8",
                                    success: function (data) {
                                        $('#deviseIdFournisseur').val(data.id);
                                    }
                                });
                            }
                        });
                    }

                    function loadDevise(options) {
                        $('#devise').autocomplete({
                            lookup: options,
                            onSelect: function (suggestion) {
                                var id = parseInt(suggestion.data);
                                $.ajax({
                                    type: "GET",
                                    dataType: "json",
                                    url: contextPath + "/Devise/get/" + id,
                                    contentType: "application/json;charset-UTF-8",
                                    success: function (data) {
                                        $('#deviseId').val(data.id);
                                    }
                                });
                            }
                        });
                    }
                });

                $(document).ready(function () {
                    var lineId = parseInt($('#lineID').text());
                    var tauxConv = {};
                    getAllBesoin();
                    getAllBudget();
                    getAllActivity();
                    $('#ajouterDevise').click(function () {
                        tauxConv.taux = $('#tauxConv').val();

                        var tauxJson = JSON.stringify(tauxConv);
                        var id = $('#deviseId').val();
                        $.ajax({
                            url: contextPath + '/TauxConversion/Ajouter/' + lineId + '/' + id,
                            method: 'POST',
                            data: tauxJson,
                            contentType: "application/json; charset=utf-8",
                            success: function (data) {
                                getAllBesoin();
                                getAllBudget();
                                getAllActivity();
                                $('#DeviseSuccess').text(data.devise.libelle);
                                $('#exampleModalCenter2').modal('hide');
                                $('#exampleModalCenter17').modal('show');
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
                    });
                });

                $(document).ready(function () {
                    var arrayTaux = [];
                    $.ajax({
                        url: contextPath + "/TauxConv/All",
                        async: true,
                        dataType: 'json',
                        success: function (data) {
                            for (var i = 0, len = data.length; i < len; i++) {
                                var id = (data[i].id).toString();
                                arrayTaux.push({ 'value': data[i].devise.code.trim(), 'data': id });
                            }
                            loadTaux(arrayTaux);
                        }
                    });

                    function loadTaux(options) {
                        $('#deviseList').autocomplete({
                            lookup: options,
                            onSelect: function (suggestion) {
                                $('#deviseList').val(suggestion.value);
                            }
                        });
                    }
                });

                function modalCalculateQuantite() {
                    $('#exampleModalCenterCalcul').modal('show');
                }

                function dimension() {
                    var largeur = document.getElementById('Largeur').value;
                    var longueur = document.getElementById('Longueur').value;
                    var hauteur = document.getElementById('Hauteur').value;
                    var quantiteDim = document.getElementById('dimQuantite').value;
                    var resultat = document.getElementById('resultat');
                    var largeurEdit = document.getElementById('largeurEdit').value;
                    var longueurEdit = document.getElementById('longueurEdit').value;
                    var hauteurEdit = document.getElementById('hauteurEgit').value;
                    var muliplication = document.getElementById('multiplicationEdit').value;
                    var quantiteEdit = document.getElementById('besoinquantite');
                    var quantite = document.getElementById('quantite');
                    var calculResultat = largeur * longueur * hauteur * quantiteDim;
                    var calculResultatEdit = largeurEdit * longueurEdit * hauteurEdit * muliplication;

                    resultat.value = calculResultat;
                    quantite.value = calculResultat;
                    quantiteEdit.value = calculResultatEdit;

                    calculateEditBesoin();
                }

                $(document).ready(function () {
                    var arrayModel = [];
                    $.ajax({
                        url: contextPath + "/Modele/All",
                        async: true,
                        dataType: 'json',
                        success: function (data) {
                            for (var i = 0, len = data.length; i < len; i++) {
                                var id = data[i].id;
                                arrayModel.push({ 'value': data[i].designation.trim(), 'data': id });
                            }
                            loadArticle(arrayModel);
                        }
                    });

                    function loadArticle(options) {
                        $('#modeleList').autocomplete({
                            lookup: options,
                            onSelect: function (suggestion) {
                                var id = parseInt(suggestion.data);
                                getModel(id);
                            }
                        });
                    }
                });

                function getModel(id) {
                    var besoinBody = $('#besoinList tbody');
                    var moBody = $('#moList tbody');
                    var budgetBody = $('#budgetList tbody');
                    $.ajax({
                        url: contextPath + '/Modele/' + id,
                        method: 'GET',
                        dataType: 'json',
                        success: function (data) {
                            besoinBody.empty();
                            moBody.empty();
                            budgetBody.empty();
                            besoinBody.append('<tr>' +
                                ' <td><strong>Désignation</strong></td>' +
                                '  <td><strong>Quantité</strong></td>' +
                                '  <td><strong>Chute %</strong></td>' +
                                '<td><strong>Largeur</strong></td>' +
                                ' <td><strong>Longueur</strong></td>' +
                                '<td><strong>Hauteur</strong></td>' +
                                '<td><strong>Unité</strong></td>' +
                                '<td><strong>CR</strong></td>' +
                                '<td><strong>Total CR</strong></td>' +
                                '<td><strong>CR saisi</strong></td>' +
                                '<td><strong>Total CR Saisi</strong></td>' +
                                '</tr>');
                            moBody.append('<tr>' +
                                ' <td><strong>Fonction</strong></td>' +
                                '  <td><strong>Unité</strong></td>' +
                                '  <td><strong>Quantité</strong></td>' +
                                '<td><strong>CR</strong></td>' +
                                ' <td><strong>Total CR</strong></td>' +
                                '</tr>');
                            budgetBody.append('<tr>' +
                                ' <td><strong>Budget</strong></td>' +
                                '  <td><strong>Libellé budget</strong></td>' +
                                '  <td><strong>Quantité</strong></td>' +
                                '<td><strong>CR unitaire</strong></td>' +
                                ' <td><strong>Total coût revient</strong></td>' +
                                '<td><strong>% marge</strong></td>' +
                                '<td><strong>TOT CR vente</strong></td>' +
                                '<td><strong>Unité de vente</strong></td>' +
                                '</tr>');
                            $(data.besoinModeles).each(function (index, element) {
                                besoinBody.append('<tr>' +
                                    '<td>' + element.designationBes + '</td>' +
                                    '<td>' + Number.parseFloat(element.quantite).toFixed(2) + '</td>' +
                                    '<td>' + Number.parseFloat(element.chute).toFixed(1) + '</td>' +
                                    '<td>' + element.largeur + '</td>' +
                                    '<td>' + element.longueur + '</td>' +
                                    '<td>' + element.hauteur + '</td>' +
                                    '<td>' + element.uniteRef + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRev) + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRevTotal) + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRevSaisi) + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRevSaisiTotal) + '</td>' +
                                    '</tr>');
                            });
                            $(data.activiteModeles).each(function (index, element) {
                                moBody.append('<tr>' +
                                    '<td>' + element.fonction.libelle + '</td>' +
                                    '<td>' + element.unite + '</td>' +
                                    '<td>' + Number.parseFloat(element.quantite).toFixed(2) + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRev) + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRevTotal) + '</td>' +
                                    '</tr>');
                            });
                            $(data.budgetAffaireModeles).each(function (index, element) {
                                budgetBody.append('<tr>' +
                                    '<td>' + element.budget.codebudget + '</td>' +
                                    '<td>' + element.budget.nom + '</td>' +
                                    '<td>' + Number.parseFloat(element.quantite).toFixed(2) + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.crunitaire) + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.totalCoutRe) + '</td>' +
                                    '<td>' + Number.parseFloat(element.marge).toFixed(0) + '%' + '</td>' +
                                    '<td>' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.crmarge) + '</td>' +
                                    '<td>' + element.uniteVente + '</td>' +
                                    '</tr>');
                            });

                            $('#crModel').text(Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(data.bordDetail.prixHT));
                            $('#modelRemise').text(Number.parseFloat(data.bordDetail.rabais).toFixed(2));
                            $('#crRemisemodel').text(Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(data.bordDetail.afterRabais));
                            $('#idModel').text(id);
                        },
                        error: function (error) {
                            // Gérer l'erreur si nécessaire
                        }
                    });
                }

                $(document).ready(function () {
                    $('#dispatcher').on('submit', function (e) {
                        e.preventDefault();
                        var lineBord = parseInt($('#lineID').text());
                        var idModel = parseInt($('#idModel').text());

                        console.log(lineBord + " model " + idModel);

                        $.ajax({
                            url: contextPath + "/ModeleDispatch/" + lineBord + "/" + idModel,
                            method: "POST",
                            contentType: "application/json; charset=utf-8",
                            success: function (data) {
                                window.location.href = 'http://localhost:8080' + contextPath + '/sous-Detail/' + lineBord;
                            },
                            error: function (e) {
                                alert("ERROR : ", e);
                            }
                        });
                    });
                });

$(document).ready(function () {
    // Attachement explicite des écouteurs d'événements aux champs
    $(document).on('input change', '#heures', function() {
        console.log("Heures changées: " + this.value);
        convertToDecimal();
    });

    $(document).on('input change', '#minutes', function() {
        console.log("Minutes changées: " + this.value);
        convertToDecimal();
    });

    // Écouteur pour mettre à jour la page quand localStorage change
    window.addEventListener('storage', function(e) {
        if (e.key === 'globalAppSync') {
            try {
                var lastSync = parseInt(e.newValue);
                var now = new Date().getTime();
                if (!isNaN(lastSync) && (now - lastSync < 3000)) {
                    console.log("Synchronisation détectée dans chiffrage.js, rechargement de la page...");

                    // Attendre un court instant pour permettre à d'autres opérations de se terminer
                    setTimeout(function() {
                        location.reload();
                    }, 300);
                }
            } catch(e) {
                console.warn("Erreur lors du traitement de l'événement de synchronisation:", e);
            }
        }
    });

    // Vérification périodique pour les modifications qui ont pu être manquées
    function checkForMissedUpdates() {
        try {
            var lastSync = localStorage.getItem('globalAppSync');
            if (lastSync) {
                var lastSyncTime = parseInt(lastSync);
                var now = new Date().getTime();
                var timeDiff = now - lastSyncTime;

                // Si la dernière synchronisation date de moins de 5 secondes
                // mais plus de 2 secondes (pour éviter les rechargements en cascade)
                if (!isNaN(lastSyncTime) && timeDiff < 5000 && timeDiff > 2000) {
                    console.log("Mise à jour récente détectée, rechargement de la page...");
                    location.reload();
                }
            }
        } catch(e) {
            console.warn("Erreur lors de la vérification des mises à jour manquées:", e);
        }
    }

    // Fonction principale pour gérer fournisseur selon type
    window.toggleFournisseurField = function() {
        console.log('toggleFournisseurField appelée');

        const typeArticle = document.getElementById('typeArticle');
        const fournisseurField = document.getElementById('fournisseur');

        if (!typeArticle || !fournisseurField) {
            console.log('Éléments non trouvés:', {
                typeArticle: !!typeArticle,
                fournisseurField: !!fournisseurField
            });
            return;
        }

        const typeValue = typeArticle.value;
        console.log('Type sélectionné:', typeValue);

        if (typeValue === 'fabrique') {
            // Article fabriqué : désactiver
            fournisseurField.disabled = true;
            fournisseurField.value = '';
            fournisseurField.placeholder = 'Non applicable (Article fabriqué)';
            fournisseurField.style.backgroundColor = '#f8f9fa';
            document.getElementById('fournisseurID').innerHTML = '';

            // Désactiver le bouton +
            const boutonPlus = document.querySelector('a[data-target="#exampleModalCenter"]');
            if (boutonPlus) {
                boutonPlus.style.pointerEvents = 'none';
                boutonPlus.style.opacity = '0.5';
            }
            console.log('Article fabriqué - fournisseur désactivé');

        } else if (typeValue === 'achete') {
            // Article acheté : ACTIVER
            fournisseurField.disabled = false;
            fournisseurField.placeholder = '';
            fournisseurField.style.backgroundColor = 'white';

            // RÉACTIVER le bouton +
            const boutonPlus = document.querySelector('a[data-target="#exampleModalCenter"]');
            if (boutonPlus) {
                boutonPlus.style.pointerEvents = 'auto';
                boutonPlus.style.opacity = '1';
            }
            console.log('Article acheté - fournisseur activé');

        } else {
            // Aucun type : désactiver
            fournisseurField.disabled = true;
            fournisseurField.value = '';
            fournisseurField.placeholder = 'Sélectionner d\'abord un type d\'article';
            fournisseurField.style.backgroundColor = '#f8f9fa';

            const boutonPlus = document.querySelector('a[data-target="#exampleModalCenter"]');
            if (boutonPlus) {
                boutonPlus.style.pointerEvents = 'none';
                boutonPlus.style.opacity = '0.5';
            }
            console.log('Aucun type - tout désactivé');
        }
    };

// Validation avant sélection d'article
    $(document).ready(function() {
        console.log('Document ready - initialisation...');

        // Attendre que tous les éléments soient chargés
        setTimeout(function() {
            console.log('Initialisation différée...');

            // Vérifier et initialiser
            if (document.getElementById('typeArticle')) {
                console.log('Element typeArticle trouvé');
                window.toggleFournisseurField();
            } else {
                console.log('Element typeArticle NON trouvé');
            }

            // Validation focus sur article
            $('#articl').on('focus', function() {
                const typeArticle = document.getElementById('typeArticle').value;
                console.log('Focus sur article, type:', typeArticle);
                if (!typeArticle) {
                    alert('Veuillez d\'abord sélectionner un type d\'article');
                    this.blur();
                    return;
                }
            });

            // Modifier le comportement du bouton Ajouter
            const btnAjouter = document.getElementById('addBesoin');
            if (btnAjouter) {
                console.log('Bouton ajouter trouvé, modification...');

                // Sauvegarder l'ancien onclick s'il existe
                const ancienOnclick = btnAjouter.onclick;

                btnAjouter.onclick = function(e) {
                    console.log('Bouton ajouter cliqué');

                    const typeArticle = document.getElementById('typeArticle').value;
                    const articleId = document.getElementById("articleId").innerHTML;
                    const lineId = document.getElementById("lineID").innerHTML;
                    const fournisseurId = document.getElementById("fournisseurID").innerHTML;

                    console.log('Données:', { typeArticle, articleId, lineId, fournisseurId });

                    // Validations
                    if (!typeArticle) {
                        alert('Veuillez sélectionner un type d\'article');
                        return false;
                    }

                    if (!articleId || articleId === '') {
                        alert('Veuillez sélectionner un article');
                        return false;
                    }

                    // Préparer les données
                    let besoinData = {
                        referenceBes: '',
                        designationBes: document.getElementById('valueArticle').value,
                        uniteRef: document.getElementById('unite').value,
                        sRef1: '',
                        sRef2: '',
                        quantite: parseFloat(document.getElementById('quantite').value) || 0,
                        chute: parseFloat(document.getElementById('chute').value) || 0,
                        largeur: parseFloat(document.getElementById('Largeur').value) || 0,
                        longueur: parseFloat(document.getElementById('Longueur').value) || 0,
                        hauteur: parseFloat(document.getElementById('Hauteur').value) || 0,
                        multiplication: parseFloat(document.getElementById('dimQuantite').value) || 1,
                        coutRev: parseFloat(document.getElementById('coutRev').value) || 0,
                        coutRevSaisi: parseFloat(document.getElementById('coutRevSais').value) || 0,
                        coutRevTotal: parseFloat(document.getElementById('coutRevTotal').value) || 0,
                        coutRevSaisiTotal: parseFloat(document.getElementById('coutRevientSaisi').value) || 0
                    };

                    // Choisir l'URL selon le type
                    let url;
                    if (typeArticle === 'achete') {
                        if (fournisseurId && fournisseurId !== '') {
                            url = '/Besoin/Ajouter/' + lineId + '/' + articleId + '?fournisseur=' + fournisseurId;
                        } else {
                            url = '/Besoin/Ajouter/' + lineId + '/' + articleId;
                        }
                    } else if (typeArticle === 'fabrique') {
                        url = '/Besoin/AjouterFabrique/' + lineId + '/' + articleId;
                    }

                    console.log('URL:', url);

                    // Appel AJAX
                    $.ajax({
                        url: url,
                        type: 'POST',
                        contentType: 'application/json',
                        data: JSON.stringify(besoinData),
                        success: function(response) {
                            console.log('Succès:', response);
                            document.getElementById("besoinArticle").innerHTML = response.designationBes;
                            $('#exampleModalCenter7').modal('show');

                            // Recharger la page ou le tableau
                            setTimeout(function() {
                                location.reload();
                            }, 1500);
                        },
                        error: function(xhr, status, error) {
                            console.error('Erreur AJAX:', { xhr, status, error });
                            alert('Erreur lors de l\'ajout: ' + error);
                        }
                    });

                    return false;
                };
            } else {
                console.log('Bouton ajouter NON trouvé');
            }

        }, 1000);
    });
});