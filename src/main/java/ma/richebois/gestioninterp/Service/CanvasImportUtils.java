package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.Model.*;
import ma.richebois.gestioninterp.Repository.AffaireRepository;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CanvasImportUtils {

    public static List<?> parseExcelFile(InputStream inputStream, Import imp) throws IOException {



        List<Ajout> listperson = new ArrayList<Ajout>();
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        List<Ajout> doubleIndividu = new ArrayList<Ajout>();


        int nombreerror = 0;

        boolean error = false;

        Workbook workbook = new XSSFWorkbook(inputStream);

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rows = sheet.iterator();

        int rowNumber = 1;

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            //skip header
            if (rowNumber == 1) {
                rowNumber++;
                continue;
            }

            Iterator<Cell> cellsInRow = currentRow.iterator();
            Ajout person = new Ajout();


            int cellIndex = 0;
            while (cellsInRow.hasNext()) {
                String message = "Erreur sur la ligne n°  " + rowNumber + " sur la cellule " + cellIndex + " du canevas ";
                Cell currentCell = cellsInRow.next();
                if (cellIndex > 24) {
                    break;
                }

                if (cellIndex == 0) {
                    try {
                        person.setMatricule((int) currentCell.getNumericCellValue());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                }
                if (cellIndex == 1) {
                    try {

                        person.setCodecontrat((int) currentCell.getNumericCellValue());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }

                }
                if (cellIndex == 2) {
                    try {

                        person.setNom(currentCell.toString().toUpperCase().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }

                } else if (cellIndex == 3) {
                    try {

                        person.setPrenom(currentCell.toString().toUpperCase().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 4) {
                    try {

                        person.setDatenaissance(currentCell.getDateCellValue());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 5) {
                    try {

                        person.setDateentree(currentCell.getDateCellValue());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 6) {
                    try {

                        person.setFonction(String.valueOf((int) currentCell.getNumericCellValue()));
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 7) {
                    try {

                        person.setAdressepays(currentCell.toString().toUpperCase().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 8) {
                    try {

                        person.setAdresseville(currentCell.toString().toUpperCase().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 9) {
                    try {

                        person.setAdresserue(currentCell.toString().toUpperCase().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 10) {
                    try {
                        person.setCodepays(currentCell.toString().toUpperCase().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 11) {
                    try {
                        person.setBanque(currentCell.toString().toUpperCase().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 12) {
                    try {
                        if (currentCell.getStringCellValue().length()<24 || currentCell.getStringCellValue().length()>24){
                            ErrorMessage errormessage = new ErrorMessage();
                            errormessage.setMessage(message+" de la valeur : "+currentCell+" Le RIB contient : "+currentCell.getStringCellValue().length());
                            errorMessages.add(errormessage);
                            error = true;
                            nombreerror++;
                        }else if (currentCell.getStringCellValue().length()==24) {
                            person.setRib(currentCell.toString().trim());
                        }
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 13) {
                    try {

                        person.setDomiciliation(currentCell.toString().toUpperCase().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 14) {
                    try {

                        if (currentCell.getCellType() == CellType.NUMERIC) {
                            person.setPcharge((int) currentCell.getNumericCellValue());
                        } else {
                            person.setPcharge(Integer.valueOf(currentCell.getStringCellValue()));
                        }
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 15) {
                    try {

                        person.setSexe(currentCell.toString().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 16) {
                    try {

                        person.setSitfamiliale(currentCell.toString().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 17) {
                    try {
                        person.setCodecin(currentCell.toString().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 18) {
                    try {
                        if (currentCell.getCellType()==CellType.STRING)
                            person.setCnss(currentCell.getStringCellValue());
                        else {
                            ErrorMessage errormessage = new ErrorMessage();
                            errormessage.setMessage( "Erreur sur la ligne n°  " + rowNumber + " du canevas : Le numéro de CNSS est incorrect");
                            errorMessages.add(errormessage);
                            error = true;
                            nombreerror++;

                        }
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage("Erreur sur la ligne n°  " + rowNumber + " sur la cellule " + cellIndex + " du canevas : Le numéro de CNSS est incorrect");
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 19) {
                    try {

                        person.setTypecontrat(currentCell.toString().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 20) {
                    try {

                        person.setProfile(currentCell.toString().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 21) {
                    try {
                        if (currentCell.getCellType() == CellType.NUMERIC) {
                            person.setSalaire(currentCell.getNumericCellValue());
                        } else {
                            person.setSalaire(Double.valueOf(currentCell.getStringCellValue()));
                        }
                    } catch (Exception e) {

                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 22) {
                    try {

                        person.setCodechantier(currentCell.toString().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 23) {
                    try {
                        if (currentCell.getCellType()==CellType.BLANK)
                            person.setNiveauetude("NULL");
                        else{
                            person.setNiveauetude(currentCell.toString().trim());
                        }

                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                } else if (cellIndex == 24) {
                    try {
                        person.setModeregl(currentCell.toString().trim());
                    } catch (Exception e) {
                        ErrorMessage errormessage = new ErrorMessage();
                        errormessage.setMessage(message+" de la valeur :"+currentCell);
                        errorMessages.add(errormessage);
                        error = true;
                        nombreerror++;
                    }
                }
                person.setImp(imp);
                person.setOrigine("importé");
                person.setState("Validé");

                person.setExist(false);
                person.setCactif(false);
                person.setCsuspendu(false);
                cellIndex++;

            }
            rowNumber++;
            if (!error && nombreerror == 0){
                listperson.add(person);
            }



        }
        workbook.close();


        if (nombreerror > 0) {
            listperson.clear();
            return (List<?>) errorMessages;
        } else if (nombreerror == 0) {
            errorMessages.clear();
            return (List<?>) listperson;
        } else {
            return null;
        }

    }


}
