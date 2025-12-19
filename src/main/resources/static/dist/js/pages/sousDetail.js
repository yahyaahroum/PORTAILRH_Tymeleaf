
function refreshConsultationView() {
    if (window.opener && !window.opener.closed) {
        window.opener.location.reload();
    } else {
        localStorage.setItem('refreshConsultation', new Date().getTime().toString());
    }
}

function Niveau(id) {
    var niveau = $('#selectNiveau').val();
    var tableBody = $('#recap tbody');
    var bordereauID = $('#bordereauIdRecap');
    bordereauID.val(id);
    tableBody.empty();
    $.ajax({
        url: contextPath+'/BordDetail/NiveauList/' + id,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            var selectNiveau = $('#selectNiveau');
            selectNiveau.empty();
            selectNiveau.append('<option value=""> Selectionner un niveau </option>');
            tableBody.append('<tr ><td colspan="7" align="center"><strong>Aucune récaptulation</strong></td></tr>');
            $(data).each(function (index, element) {
                selectNiveau.append('<option value="' + element + '">' + element + '</option>');
            })

        },
        error: function (error) {
            console.error("Erreur lors du chargement des niveaux:", error);
            alert("Une erreur s'est produite lors du chargement des niveaux. Veuillez réessayer.");
        }
    });
}

function NiveauBudget(id) {
    var niveau = $('#selectNiveauBudget').val();
    var tableBody = $('#Marge tbody');
    var bordereauID = $('#bordereauIdForMarge');
    bordereauID.val(id);
    tableBody.empty();
    $.ajax({
        url: contextPath+'/BordDetail/NiveauList/' + id,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            var selectNiveau = $('#selectNiveauBudget');
            selectNiveau.empty();
            selectNiveau.append('<option value=""> Selectionner un niveau </option>');
            tableBody.append('<tr ><td colspan="6" align="center"><strong>Aucun Budget</strong></td></tr>');
            $(data).each(function (index, element) {
                selectNiveau.append('<option value="' + element + '">' + element + '</option>');

            })

        },
        error: function (error) {
            console.error("Erreur lors du chargement des niveaux pour le budget:", error);
            alert("Une erreur s'est produite lors du chargement des niveaux. Veuillez réessayer.");
        }
    });
}

$(document).ready(function () {

    $('#selectNiveauBudget').change(function () {
        var niveau = $('#selectNiveauBudget').val();
        var bordereauID = $('#bordereauIdForMarge').val();
        if (niveau == '') {
            var tableBody = $('#Marge tbody');
            tableBody.empty();
            tableBody.append('<tr><td align="center" colspan="6"><strong>Aucun Budget</strong></td></tr>');
        } else
            $.ajax({
                url: contextPath+'/Bordereau/budgetForMarge/' + bordereauID + '?niveau=' + niveau,
                method: 'GET',
                dataType: 'json',
                success: function (data) {
                    var tableBody = $('#Marge tbody');
                    tableBody.empty();
                    $(data).each(function (index, element) {
                        tableBody.append('<tr id="tr' + element.budget.id + element.numLine + '"><td>' + element.lot + '</td>' +
                            '<td >' + element.budget.nom + '</td>' +
                            '<td >' + Intl.NumberFormat('de-DE', {
                                style: 'currency',
                                currency: 'MAD'
                            }).format(element.coutRev) + '</td>' +

                            '<td id="' + element.budget.id + element.numLine + '">' + Number.parseFloat(element.marge).toFixed(0) + ' %' + '</td>' +
                            '<td >' + Intl.NumberFormat('de-DE', {
                                style: 'currency',
                                currency: 'MAD'
                            }).format(element.mntMarge) + '</td>' +
                            '<td >' + Intl.NumberFormat('de-DE', {
                                style: 'currency',
                                currency: 'MAD'
                            }).format(element.crMarge) + '</td>' +
                            '<td id="MargeButton' + element.budget.id + element.numLine + '"><button type="button" class="btn-xs btn-success" title="Modifier la marge"  onclick="getMargeInput(' + element.marge + ',' + element.budget.id + ',' + element.numLine + ')"><i class="fa fa-pen"></i></button>' +
                            '</td></tr>');
                    })
                    if (typeof window.syncApp === 'function') {
                        window.syncApp();
                    }
                },
                error: function (error) {
                    console.error("Erreur lors du chargement des budgets:", error);
                    alert("Une erreur s'est produite lors du chargement des budgets. Veuillez réessayer.");
                }
            });


    })
});

function getRabaisInput(id) {
    var tdRabais = $('#' + id);
    var bordereauID = $('#bordereauIdRecap').val();
    var ligne = id;
    $.ajax({
        url: contextPath+'/BordDetail/' + bordereauID + '?ligne=' + ligne,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            tdRabais.empty();
            tdRabais.append('<div class="input-group mb-3">' +
                '                    <input type="number" class="form-control-sm" max="100" min="0" id="rabaisvalue' + id + '" value="' + data.rabais + '"/>' +
                '                    <div class="input-group-append">' +
                '                        <div class="btn-group">' +
                '                            <a  type="button" onclick="appRabais(' + id + ')"><i class="fas fa-check-circle"></i></a>' +
                '                        </div>' +
                '                    </div>' +
                '                </div>');
        },
        error: function (error) {
            console.error("Erreur lors de la récupération du rabais:", error);
            alert("Droit d'accés refusés");
        }
    });

}

function getMargeInput(marge, budget, numLine) {
    var tdMarge = $('#' + budget + numLine);
    var marge = marge;
    var buttontosave = $('#MargeButton' + budget + numLine);
    buttontosave.empty();
    buttontosave.append('<button type="button" class="btn-xs btn-success" title="Modifier la marge"  onclick="appMarge(' + budget + ',' + numLine + ')"><i class="fa fa-plus"></i></button>');
    tdMarge.empty();
    tdMarge.append('<div class="input-group mb-3">' +
        '                    <input type="number" class="form-control-sm" max="100" min="0" id="margevalue' + budget + numLine + '" value="' + Number.parseFloat(marge).toFixed(0) + '"/>' +
        '                </div>');
}

function appRabais(id) {
    $('#exampleModalCenter7').modal('show');
    var niveau = $('#selectNiveau').val();
    var ligne = id;
    var rabais = $('#rabaisvalue' + id).val();
    var bordereauID = $('#bordereauIdRecap').val();
    console.log('rabais : ' + rabais + ' ligne : ' + ligne + ' niveau : ' + niveau);

    $.ajax({
        url: contextPath+'/BordDetail/Rabais/' + bordereauID + '?niveau=' + niveau + '&ligne=' + ligne + '&rabais=' + rabais,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            var tableBody = $('#recapligne' + id);
            tableBody.empty();

            tableBody.append('<td>' + data.designation + '</td>' +
                '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(data.coutRevient) + '</td>' +
                '<td >' + Number.parseFloat(data.marge).toFixed(2) + ' %' + '</td>' +
                '<td align="center">' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(data.mntMarge) + '</td>' +
                '<td align="center">' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(data.sumPrixHT) + '</td>' +
                '<td id="' + data.ligne_bord + '" align="center">' + Number.parseFloat(data.rabais).toFixed(1) + ' %  ' + '<a type="button" class="btn-xs btn-info" onclick="getRabaisInput(' + data.ligne_bord + ')"><i class="fa fa-pen"></i></a></td>' +
                '<td align="center">' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(data.mntAfterrabais) + '</td>');

            setTimeout(function () {
                $('#exampleModalCenter7').modal('hide');
            }, 300);

            tableBody.addClass("btn-warning");
            $('#exampleModalCenter6').modal('show');

            if (typeof window.syncApp === 'function') {
                window.syncApp();
            } else if (typeof refreshConsultationView === 'function') {
                refreshConsultationView();
            }
        },
        error: function (xhr, status, error) {
            console.error("Erreur AJAX:", xhr.status, error);
            $('#exampleModalCenter7').modal('hide');
            alert("Une erreur s'est produite lors de l'application du rabais. Veuillez réessayer.");
        }
    });
}

function appMarge(budget, numLine) {
    $('#exampleModalCenter7').modal('show');
    var niveau = $('#selectNiveauBudget').val();
    var marge = $('#margevalue' + budget + numLine).val();

    var bordereauID = $('#bordereauIdForMarge').val();
    $.ajax({
        url: contextPath+'/Bordereau/budgetAppForMarge/' + bordereauID + '?niveau=' + niveau + '&budget=' + budget + '&numLine=' + numLine + '&marge=' + marge,
        method: 'GET',
        dataType: 'json',
        success: function (data) {

            var tableBody = $('#tr' + budget + numLine);
            tableBody.empty();

            console.log(data.numLine + data.lot + data.marge);

            tableBody.append('<td>' + data.lot + '</td>' +
                '<td >' + data.budget.nom + '</td>' +
                '<td >' + Intl.NumberFormat('de-DE', {
                    style: 'currency',
                    currency: 'MAD'
                }).format(data.coutRev) + '</td>' +

                '<td id="' + data.budget.id + data.numLine + '">' + Number.parseFloat(data.marge).toFixed(1) + ' %' + '</td>' +
                '<td >' + Intl.NumberFormat('de-DE', {
                    style: 'currency',
                    currency: 'MAD'
                }).format(data.mntMarge) + '</td>' +
                '<td >' + Intl.NumberFormat('de-DE', {
                    style: 'currency',
                    currency: 'MAD'
                }).format(data.crMarge) + '</td>' +
                '<td id="MargeButton' + data.budget.id + data.numLine + '"><button type="button" class="btn-xs btn-success" title="Modifier la marge"  onclick="getMargeInput(' + data.marge + ',' + data.budget.id + ',' + data.numLine + ')"><i class="fa fa-pen"></i></button>' +
                '</td>');

            setTimeout(function () {
                $('#exampleModalCenter7').modal('hide');
            }, 300);

            tableBody.addClass("btn-success");
            $('#exampleModalCenter6').modal('show');

            if (typeof window.syncApp === 'function') {
                window.syncApp();
            } else {
                refreshConsultationView();
            }        },
        error: function (error) {
            console.error("Erreur lors de l'application de la marge:", error);
            $('#exampleModalCenter7').modal('hide');
            alert("Une erreur s'est produite lors de l'application de la marge. Veuillez réessayer.");
        }
    });
}

function getModel(id) {
    MODEL_ID=id;
    var besoinBody = $('#besoinList tbody');
    var moBody = $('#moList tbody');
    var budgetBody = $('#budgetList tbody');
    $.ajax({
        url: contextPath+'/Modele/GetModelToSave/' + id,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            besoinBody.empty();
            moBody.empty();
            budgetBody.empty();
            besoinBody.append('<tr>'+
                ' <td><strong>Désignation</strong></td>' +
                '  <td><strong>Quantité</strong></td>' +
                '  <td><strong>Chute %</strong></td>' +
                '<td><strong>Largeur</strong></td>'+
                ' <td><strong>Longueur</strong></td>' +
                '<td><strong>Hauteur</strong></td>' +
                '<td><strong>Unité</strong></td>' +
                '<td><strong>CR</strong></td>' +
                '<td><strong>Total CR</strong></td>' +
                '<td><strong>CR saisi</strong></td>' +
                '<td><strong>Total CR Saisi</strong></td>' +
                '</tr>');
            moBody.append('<tr>'+
                ' <td><strong>Fonction</strong></td>' +
                '  <td><strong>Unité</strong></td>' +
                '  <td><strong>Quantité</strong></td>' +
                '<td><strong>CR</strong></td>'+
                ' <td><strong>Total CR</strong></td>' +
                '<td><strong>Budget</strong></td>' +
                '</tr>');
            budgetBody.append('<tr>'+
                ' <td><strong>Budget</strong></td>' +
                '  <td><strong>Libellé budget</strong></td>' +
                '  <td><strong>Quantité</strong></td>' +
                '<td><strong>CR unitaire</strong></td>'+
                ' <td><strong>Total coût revient</strong></td>' +
                '<td><strong>% marge</strong></td>' +
                '<td><strong>TOT CR vente</strong></td>' +
                '<td><strong>Unité de vente</strong></td>' +
                '</tr>');
            $(data.besoinList).each(function (index, element) {
                besoinBody.append('<tr>'+
                    '<td>' + element.designationBes + '</td>' +
                    '<td>' + Number.parseFloat(element.quantite).toFixed(2) + '</td>' +
                    '<td>' + Number.parseFloat(element.chute).toFixed(1) + '</td>' +
                    '<td>' + element.largeur + '</td>' +
                    '<td>' + element.longueur + '</td>' +
                    '<td>' + element.hauteur + '</td>' +
                    '<td>' + element.uniteRef + '</td>' +
                    '<td>' + Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(element.coutRev) + '</td>' +
                    '<td>' + Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(element.coutRevTotal) + '</td>' +
                    '<td>' + Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(element.coutRevSaisi) + '</td>' +
                    '<td>' + Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(element.coutRevSaisiTotal )+ '</td>' +
                    '</tr>');
            });
            $(data.activiteList).each(function (index, element) {
                moBody.append('<tr>'+
                    '<td>' + element.fonction.libelle + '</td>' +
                    '<td>' + element.unite + '</td>' +
                    '<td>' + Number.parseFloat(element.quantite).toFixed(2) + '</td>' +
                    '<td>' + Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(element.coutRev) + '</td>' +
                    '<td>' + Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(element.coutRevTotal) + '</td>' +
                    '<td>' + element.budget.codebudget + '</td>' +
                    '</tr>');
            });
            $(data.budgetAffaireList).each(function (index, element) {
                budgetBody.append('<tr>'+
                    '<td>' + element.budget.codebudget + '</td>' +
                    '<td>' + element.budget.nom + '</td>' +
                    '<td>' + Number.parseFloat(element.quantite).toFixed(2) + '</td>' +
                    '<td>' + Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(element.crunitaire) + '</td>' +
                    '<td>' + Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(element.totalCoutRe) + '</td>' +
                    '<td>' +Number.parseFloat(element.marge).toFixed(0)+ '%'  +'</td>' +
                    '<td>' +Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(element.crmarge)  + '</td>' +
                    '<td>' + element.uniteVente + '</td>' +
                    '</tr>');
            });

            $('#crModel').text(Intl.NumberFormat('de-DE', {
                style: 'currency',
                currency: 'MAD'
            }).format(data.prixHT));
            $('#modelRemise').text(Number.parseFloat(data.rabais).toFixed(2));
            $('#crRemisemodel').text(Intl.NumberFormat('de-DE', {
                style: 'currency',
                currency: 'MAD'
            }).format(data.afterRabais));
            $('#idBordLine').text(data.idBord);

            $('#exampleModalCenter2').modal('show');
        },
        error: function (error) {
            console.error("Erreur lors de la récupération du modèle:", error);
        }
    });
}

$(document).ready(function () {
    $('#selectNiveau').change(function () {
        var niveau = $('#selectNiveau').val();
        var bordereauID = $('#bordereauIdRecap').val();
        if (niveau == '') {
            var tableBody = $('#recap tbody');
            tableBody.empty();
            tableBody.append('<tr><td align="center" colspan="7"><strong>Aucune Récaptulation</strong></td></tr>');
        } else {
            $.ajax({
                url: contextPath+'/BordDetail/RecapList/' + bordereauID + '?niveau=' + niveau,
                method: 'GET',
                dataType: 'json',
                success: function (data) {
                    var tableBody = $('#recap tbody');
                    tableBody.empty();
                    $(data).each(function (index, element) {
                        tableBody.append('<tr id="recapligne' + element.ligne_bord + '"><td>' + element.designation + '</td>' +
                            '<td >' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.coutRevient) + '</td>' +
                            '<td >' + Number.parseFloat(element.marge).toFixed(2) + ' %' + '</td>' +
                            '<td align="center">' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.mntMarge) + '</td>' +
                            '<td align="center">' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.sumPrixHT) + '</td>' +
                            '<td id="' + element.ligne_bord + '" align="center">' + Number.parseFloat(element.rabais).toFixed(2) + ' %  ' + '<a type="button" class="btn-xs btn-info"' +
                            ' onclick="getRabaisInput(' + element.ligne_bord + ')"><i class="fa fa-pen"></i></a></td>' +
                            '<td align="center">' + Intl.NumberFormat('de-DE', { style: 'currency', currency: 'MAD' }).format(element.mntAfterrabais) + '</td></tr>');
                    });

                    if (typeof window.syncApp === 'function') {
                        window.syncApp();
                    }
                },
                error: function (error) {
                    console.error("Erreur lors du chargement des données récapitulatives:", error);
                    alert("Une erreur s'est produite lors du chargement des données. Veuillez réessayer.");
                }
            });
        }
    });
});

function getLastModification(id) {
    $.ajax({
        url: contextPath+'/Besoin/History/' + id,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            var tableBody = $('#LastModif tbody');
            tableBody.empty();
            $(data).each(function (index, element) {
                tableBody.append('<tr><td>' + element.createdDate + '</td>' +
                    '<td>' + element.createdBy + '</td>' +
                    '<td>' + element.libelle + '</td></tr>');
            })

        },
        error: function (error) {
            var tableBody = $('#LastModif tbody');
            tableBody.empty();
            tableBody.append(
                '<tr><td colspan="3" align="center"><strong>Aucune modification faite au niveau du cet article</strong></td></tr>'
            );
        }
    });
}

function indentationDroite(id) {
    $.ajax({
        type: 'get',
        url: contextPath+'/BordDetail/indentationDroite/' + id,
        contentType: "application/json;charset-UTF-8",
        success: function (data) {
            $('#niveau' + id).text(data.niveau);
            $('#designation' + id).text(data.designation);
            $('#quantite' + id).text(data.quantite);
            $('#prixht' + id).text(Intl.NumberFormat('de-DE', {
                style: 'currency',
                currency: 'MAD'
            }).format(data.prixHT));
            $('#pu' + id).text(Intl.NumberFormat('de-DE', {
                style: 'currency',
                currency: 'MAD'
            }).format(data.pu));

            if (typeof window.syncApp === 'function') {
                window.syncApp();
            }

        },
        error: function (error) {
            console.error("Erreur lors de l'indentation droite:", error);
        }
    });
}

function indentationGauche(id) {

    $.ajax({
        type: 'get',
        url: contextPath+'/BordDetail/indentationGauche/' + id,
        contentType: "application/json;charset-UTF-8",
        success: function (data) {
            $('#niveau' + id).text(data.niveau);
            $('#designation' + id).text(data.designation);
            $('#quantite' + id).text(data.quantite);
            $('#prixht' + id).text(Intl.NumberFormat('de-DE', {
                style: 'currency',
                currency: 'MAD'
            }).format(data.prixHT));
            $('#pu' + id).text(Intl.NumberFormat('de-DE', {
                style: 'currency',
                currency: 'MAD'
            }).format(data.pu));

            if (typeof window.syncApp === 'function') {
                window.syncApp();
            }

        },
        error: function (error) {
            console.error("Erreur lors de l'indentation gauche:", error);
            alert("Vous n'avez pas les droits de modifier");
        }
    });
}

$(document).ready(function () {

    $('#genererBudget').click(function () {
        var bordereauId = $('#bordereauIdGenererBudget').val();
        $('#exampleModalCenter3').modal('hide');
        $('#exampleModalCenter7').modal('show');
        $.ajax({
            url: contextPath+'/BordDetail/GenererBudget/' + bordereauId,
            method: 'POST',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                setTimeout(function () {
                    $('#exampleModalCenter7').modal('hide');
                }, 300);
                $('#exampleModalCenter6').modal('show');

                if (typeof window.syncApp === 'function') {
                    window.syncApp();
                }
            },
            error: function (error) {
                console.error("Erreur lors de la génération du budget:", error);
                $('#exampleModalCenter7').modal('hide');
                alert("Une erreur s'est produite lors de la génération du budget. Veuillez réessayer.");
            }
        })

    })
});

function remiseMarge(id) {
    $('#bordereauIdRemiseMarge').val(id);
    $('#exampleModalCenter4').modal('show');
}

function GenererBudgets(id) {
    $('#bordereauIdGenererBudget').val(id);
    $('#exampleModalCenter3').modal('show');
}

$(document).ready(function () {

    $('#remisemarge').click(function () {
        var bordereauId = $('#bordereauIdRemiseMarge').val();
        $('#exampleModalCenter4').modal('hide');

        var remisemargeOption = $('#remisemargeOption').val();
        var value = $('#remisemargevalue').val();

        if (remisemargeOption == 'remise') {
            var url = contextPath+'/Bordereau/Remise/' + bordereauId + '?remise=' + value;
        } else if (remisemargeOption == 'marge') {
            var url = contextPath+'/Bordereau/Marge/' + bordereauId + '?marge=' + value;
        }
        $('#exampleModalCenter7').modal('show');
        $.ajax({
            url: url,
            method: 'POST',
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                setTimeout(function () {
                    $('#exampleModalCenter7').modal('hide');
                }, 200);
                $('#exampleModalCenter6').modal('show');

                if (typeof window.syncApp === 'function') {
                    window.syncApp();
                }
            },
            error: function (error) {
                console.error("Erreur lors de l'application de la remise/marge:", error);
                $('#exampleModalCenter7').modal('hide');
                $('#exampleModalCenter8').modal('show');
            }
        })

    })
});

function showTooltip(id, element) {
    // Cache tous les autres tooltips visibles
    $('.tooltip-content').css('display', 'none');

    $.ajax({
        url: contextPath+'/BordDetail/getBudget/' + id,
        method: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            console.log(data); // Ajoutez cette ligne pour voir la réponse de l'API

            var tooltipContent = '';

            // Vérifier si les données sont vides ou non
            if (data.length === 0) {
                tooltipContent = '<strong>Pas de budget à afficher</strong>';
            } else {
                data.forEach(function (item) {
                    tooltipContent += '<strong>' + item.budget.nom + '</strong><ul>';
                    tooltipContent += '<li><strong>Coût de revient :</strong> ' + Intl.NumberFormat('de-DE', {
                        style: 'currency',
                        currency: 'MAD'
                    }).format(item.totalCoutRe) + '</li>';
                    tooltipContent += '<li><strong>Marge :</strong> ' + Number.parseFloat(item.marge).toFixed(0) + ' %</li></ul>';
                });
            }

            // Sélectionner et afficher le tooltip associé à l'élément actuel
            var tooltipDiv = $(element).find('.tooltip-content');
            tooltipDiv.html(tooltipContent);
            tooltipDiv.css({
                display: 'block',
                position: 'absolute',
                top: $(element).offset().top - tooltipDiv.outerHeight() - 200, // Positionner juste au-dessus
                left: $(element).offset().left
            });
        },
        error: function (error) {
            console.error('Erreur lors de la récupération des données pour l\'ID :', id, error);
        }
    });
}

function hideTooltip(element) {
    // Cacher la tooltip
    var tooltipDiv = $(element).find('.tooltip-content');
    tooltipDiv.css('display', 'none');
}

function pieceJointeModal(id) {
    $('#exampleModalCenter1').modal('show');
    loadPiece(id);
}

function loadPiece(id) {
    $.ajax({
        url: contextPath+'/PieceJointes/' + id,
        method: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            var tableBody = $('#pieceJointe tbody');
            tableBody.empty();
            if (data.length == 0) {
                tableBody.append('<tr><td colspan="3" align="center"><strong>Aucune piéce jointe associée à cet article</strong></td></tr>');
            } else
                $(data).each(function (index, element) {
                    tableBody.append('<tr><td><a href="' + element.url + '" target="_blank"><strong>' + element.name + '</strong></a></td>' +
                        '<td>' + element.date + '</td>' +
                        '<td>' + element.type + '</td>' +
                        '<td><button type="button" class="btn-xs btn-danger" title="Supprimer la piéce jointe" onclick="deletePieceModal(' + element.id + ')"><i class="fa fa-trash"></i></button></td>' +
                        '</tr>');
                })

            $('#idLigne').text(id);
        },
        error: function (error) {
            console.error("Erreur lors du chargement des pièces jointes:", error);
        }
    })
}

$("#ajoutPJ").click(function (event) {
    var id = parseInt($('#idLigne').text());
    //stop submit the form, we will post it manually.
    event.preventDefault();

    fire_ajax_submit();
});

function fire_ajax_submit() {
    // Get form
    var form = $('#fileUploadForm')[0];
    var id = parseInt($('#idLigne').text());
    var data = new FormData(form);
    var type = $('#typePiece').val();

    $("#ajoutPJ").prop("disabled", true);

    $.ajax({
        type: "POST",
        enctype: 'multipart/form-data',
        url: contextPath+'/PieceJointe/Upload/' + id + '?type=' + type,
        data: data,
        //http://api.jquery.com/jQuery.ajax/
        //https://developer.mozilla.org/en-US/docs/Web/API/FormData/Using_FormData_Objects
        processData: false, //prevent jQuery from automatically transforming the data into a query string
        contentType: false,
        cache: false,
        success: function (data) {
            $("#ajoutPJ").prop("disabled", false);
            loadPiece(id);
            $('#files').val('');
        },
        error: function (error) {
            $("#ajoutPJ").prop("disabled", false);
            console.error("Erreur lors de l'upload de fichier:", error);
        }
    });
}

function deletePieceModal(id) {
    $.ajax({
        url: contextPath+'/files/' + id,
        method: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            $('#depieceid').text(data.id);
            $('#pieceName').text(data.name);
            $('#exampleModalCenter91').modal('show');
        },
        error: function (error) {
            console.error("Erreur lors de la récupération des informations de la pièce:", error);
        }
    })
}

$(document).ready(function () {
    $('#deletePiece').click(function () {
        var id = parseInt($('#depieceid').text());
        var idLine = parseInt($('#idLigne').text());
        var filename = $('#pieceName').text();

        $.ajax({
            url: contextPath+'/files/delete/' + idLine + '?fileName=' + filename,
            method: 'DELETE',
            contentType: "application/json; charset=utf-8",
            success: function () {
                loadPiece(idLine);
                $('#exampleModalCenter91').modal('hide');
            },
            error: function (error) {
                console.error("Erreur lors de la suppression de la pièce jointe:", error);
                alert("Une erreur s'est produite lors de la suppression de la pièce jointe. Veuillez réessayer.");
            }
        })
    })
})

function budgetPersonModal(id) {
    $('#bordereauIdBPerso').val(id);
    // $('#modal-xl2').modal('show');
}

function ControleBrD(id) {
    $('#exampleModalCenterControle').modal('show');
}

function ControleBrDLunch(id) {
    $('#exampleModalCenterControle').modal('hide');
    $('#exampleModalCenter7').modal('show');
    getAllControl(id);
}

function getAllControl(id) {
    $.ajax({
        url: contextPath+'/Bordereau/Controle/' + id,
        method: 'GET',
        dataType: 'json',
        success: function (data) {
            $('#exampleModalCenter7').modal('hide');
            $('#modal-xl3').modal('show');
            $('#bordereauIdControleState').val(id);
            var tableBody = $('#controleBr tbody');
            tableBody.empty();
            $(data).each(function (index, element) {
                tableBody.append('<tr>' +
                    '<td>' + element.type + '</td>' +
                    '<td>' + element.message + '</td>' +
                    '</tr>');
            })
        },
        error: function (error) {
            console.error("Erreur lors du contrôle du bordereau:", error);
            $('#exampleModalCenter7').modal('hide');
            alert("Une erreur s'est produite lors du contrôle du bordereau. Veuillez réessayer.");
        }
    });
}