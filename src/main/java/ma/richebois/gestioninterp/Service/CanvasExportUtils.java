package ma.richebois.gestioninterp.Service;

import ma.richebois.gestioninterp.DTO.AvanceDTO;
import ma.richebois.gestioninterp.Model.*;

import ma.richebois.gestioninterp.Repository.ContratRepository;
import ma.richebois.gestioninterp.Repository.IndividuRepository;
import ma.richebois.gestioninterp.Repository.OperationCaisseRepository;
import ma.richebois.gestioninterp.Repository.RIBRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
public class CanvasExportUtils {
	@Autowired
    private IndividuRepository individuRepository;
	@Autowired
    private ContratRepository contratRepository;
	@Autowired
    private RIBRepository ribRepository;
	@Autowired
    private OperationCaisseRepository operationCaisseRepository;



    private XSSFWorkbook workbook;
    private XSSFSheet individu;
    private XSSFSheet contrat;
    private XSSFSheet enfants;
    private XSSFSheet rib;
    private XSSFSheet elementvariable;
    private XSSFSheet constantes;
    private XSSFSheet cpabsences;
    private XSSFSheet emploi;
    private XSSFSheet canvasImport;
    private XSSFSheet stc;
    private XSSFSheet avance;

    DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    public CanvasExportUtils() {}

    private String FormatterMatricule(int param){
        String matricule = Integer.toString(param);
        if (param<=9){
            matricule = "000"+param;
        } else if (param<=99){
            matricule = "00"+param;
        }else if (param<=999){
            matricule = "0"+param;
        }
        return matricule;
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
//        individu.autoSizeColumn(columnCount);
//        contrat.autoSizeColumn(columnCount);
////        enfants.autoSizeColumn(columnCount);
////        elementvariable.autoSizeColumn(columnCount);
////        cpabsences.autoSizeColumn(columnCount);
////        emploi.autoSizeColumn(columnCount);
////        canvasImport.autoSizeColumn(columnCount);
////        constantes.autoSizeColumn(columnCount);

        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);

        } else if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }else if (value instanceof Date){
            cell.setCellValue((Date) value);
        }
        else  {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    public void writeHeaderLines(){
        workbook = new XSSFWorkbook();
        writeHeaderLineIndividu();
        writeHeaderLineContrat();
        writeHeaderLineEnfants();
        writeHeaderLineConstantes();
        writeHeaderRIB();
    }

    private void Canvas_importer(){
        workbook = new XSSFWorkbook();
        canvasImport = workbook.createSheet("Riche Bois");

        Row rowimport = canvasImport.createRow(0);
        CellStyle styleimport = workbook.createCellStyle();
        XSSFFont fontimport = workbook.createFont();

        fontimport.setFontName("Calibri");
        fontimport.setBold(true);
        fontimport.setFontHeight(11);

        styleimport.setFont(fontimport);
        styleimport.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleimport.setFillPattern(FillPatternType.BIG_SPOTS);
        styleimport.setAlignment(HorizontalAlignment.CENTER);

        createCell(rowimport,0,"Matricule",styleimport);
        createCell(rowimport,1,"Contrat",styleimport);
        createCell(rowimport,2,"Nom",styleimport);
        createCell(rowimport,3,"Prénom",styleimport);
        createCell(rowimport,4,"Date naissance",styleimport);
        createCell(rowimport,5,"Date d'embauche",styleimport);
        createCell(rowimport,6,"Fonction",styleimport);
        createCell(rowimport,7,"Adresse.pays",styleimport);
        createCell(rowimport,8,"Adresse.ville",styleimport);
        createCell(rowimport,9,"Adresse.rue",styleimport);
        createCell(rowimport,10,"Codepays",styleimport);
        createCell(rowimport,11,"Banque",styleimport);
        createCell(rowimport,12,"RIB",styleimport);
        createCell(rowimport,13,"Domiciliation",styleimport);
        createCell(rowimport,14,"Personnes à charge",styleimport);
        createCell(rowimport,15,"Sexe",styleimport);
        createCell(rowimport,16,"Situation Familiale",styleimport);
        createCell(rowimport,17,"CIN",styleimport);
        createCell(rowimport,18,"CNSS",styleimport);
        createCell(rowimport,19,"Type de contrat",styleimport);
        createCell(rowimport,20,"Profil",styleimport);
        createCell(rowimport,21,"Salaire",styleimport);
        createCell(rowimport,22,"Chantier",styleimport);
        createCell(rowimport,23,"Niveau d'étude",styleimport);
        createCell(rowimport,24,"Mode de réglement",styleimport);

    }


    private void writeHeaderLineIndividu(){

        individu = workbook.createSheet("Individu");

        Row rowIndividu = individu.createRow(0);

        CellStyle styleYallow = workbook.createCellStyle();

        CellStyle stylePurple = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setFontName("Calibri");
        font.setFontHeight(11);
        font.setBold(true);

        styleYallow.setFont(font);
        styleYallow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleYallow.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        stylePurple.setFont(font);
        stylePurple.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        stylePurple.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(rowIndividu,0,"FICHE",styleYallow);

        createCell(rowIndividu,1,"INDIVIDU",stylePurple);
        createCell(rowIndividu,2,"INDIVIDU.CIVILITE",stylePurple);
        createCell(rowIndividu,3,"INDIVIDU.NOMFAMILLE",stylePurple);
        createCell(rowIndividu,4,"PINDIVIDU.NOMUSAGE",stylePurple);
        createCell(rowIndividu,5,"INDIVIDU.PRENOM",stylePurple);
        createCell(rowIndividu,6,"INDIVIDU.PRENOMS",stylePurple);
        createCell(rowIndividu,7,"INDIVIDU.SEXE",stylePurple);
        createCell(rowIndividu,8,"INDIVIDU.DATENAISSANCE",stylePurple);
        createCell(rowIndividu,9,"INDIVIDU.COMMUNENAISSANCE",stylePurple);
        createCell(rowIndividu,10,"INDIVIDU.DEPARTEMENTNAISSANCE",stylePurple);
        createCell(rowIndividu,11,"INDIVIDU.CODEPAYSNATIONALITE",stylePurple);
        createCell(rowIndividu,12,"INDIVIDU.NOSS",stylePurple);
        createCell(rowIndividu,13,"INDIVIDU.SITUATIONFAMILIALE",stylePurple);
        createCell(rowIndividu,14,"INDIVIDU.RUE(1)",stylePurple);
        createCell(rowIndividu,15,"INDIVIDU.LOCALITE(1)",stylePurple);
        createCell(rowIndividu,16,"INDIVIDU.DEPARTEMENT(1)",stylePurple);
        createCell(rowIndividu,17,"INDIVIDU.VILLE(1)",stylePurple);
        createCell(rowIndividu,18,"INDIVIDU.PAYS(1)",stylePurple);
        createCell(rowIndividu,19,"INDIVIDU.up_CIN",stylePurple);

        createCell(rowIndividu,20,"INDIVIDU.TEL",stylePurple);
        createCell(rowIndividu,21,"INDIVIDU.UP_DATE_EXPIRATION_CIN",stylePurple);

    }

    private void writeDataIndividu(List<Ajout> personList){

        int rowCount = 1;
        CellStyle styleData = workbook.createCellStyle();
        XSSFFont fontData = workbook.createFont();

        fontData.setFontName("Calibri");
        fontData.setFontHeight(11);

        styleData.setFont(fontData);


        for (Ajout p : personList){

            Integer individuCount = individuRepository.countByIndividu(p.getMatricule());

            if (individuCount==0){
                Row row= individu.createRow(rowCount++);

                createCell(row,0,"INDIVIDU",styleData);
                createCell(row,1,FormatterMatricule(p.getMatricule()),styleData);

                if (p.getSexe().toLowerCase().equals("homme") || p.getSexe().equals("1")){
                    createCell(row,2,"01",styleData);
                    createCell(row,7,"01",styleData);
                }else if (p.getSexe().toLowerCase().equals("femme") || p.getSexe().equals("2")){
                    createCell(row,2,"02",styleData);
                    createCell(row,7,"02",styleData);
                }

                createCell(row,3,p.getNom(),styleData);
                createCell(row,4,p.getNom(),styleData);
                createCell(row,5,p.getPrenom(),styleData);
                createCell(row,6,p.getPrenom(),styleData);
                createCell(row,8,dateFormatter.format( p.getDatenaissance()),styleData);
                createCell(row,9,"",styleData);
                createCell(row,10,"",styleData);
                createCell(row,11,"MA",styleData);

                if (p.getCnss().equals(null) || p.getCnss().equals("")){
                    createCell(row,12,"000000000",styleData);
                }else
                    createCell(row,12,p.getCnss(),styleData);

                if (p.getSitfamiliale().toLowerCase().equals("célibataire") || p.getSitfamiliale().toLowerCase().equals("celibataire") || p.getSitfamiliale().equals("1")){
                    createCell(row,13,"01",styleData);
                } else if (p.getSitfamiliale().toLowerCase().equals("marié") || p.getSitfamiliale().toLowerCase().equals("marie") || p.getSitfamiliale().equals("2")){
                    createCell(row,13,"02",styleData);
                }else if (p.getSitfamiliale().toLowerCase().equals("divorcé") || p.getSitfamiliale().toLowerCase().equals("divorce") || p.getSitfamiliale().equals("3")){
                    createCell(row,13,"03",styleData);
                }
                else if (p.getSitfamiliale().toLowerCase().equals("veuf") || p.getSitfamiliale().toLowerCase().equals("veuf") || p.getSitfamiliale().equals("4")){
                    createCell(row,13,"04",styleData);
                }

                createCell(row,14,p.getAdresserue(),styleData);
                createCell(row,15,"",styleData);
                createCell(row,16,"",styleData);
                createCell(row,17,p.getAdresseville(),styleData);
                createCell(row,18,p.getAdressepays().substring(0,2).toUpperCase(),styleData);
                createCell(row,19,p.getCodecin().replaceAll("\\s", ""),styleData);
                createCell(row,20,p.getNumtele(),styleData);
                LocalDate date = p.getDateExpCin();
                Date outDate = java.sql.Date.valueOf(date);
                createCell(row, 21, dateFormatter.format(outDate), styleData);


            }
        }
    }

    private void writeHeaderRIB() {
        rib = workbook.createSheet("RIB");

        Row rowIndividu = rib.createRow(0);

        CellStyle styleYallow = workbook.createCellStyle();

        CellStyle stylePurple = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setFontName("Calibri");
        font.setFontHeight(11);
        font.setBold(true);

        styleYallow.setFont(font);
        styleYallow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleYallow.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        stylePurple.setFont(font);
        stylePurple.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        stylePurple.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(rowIndividu,0,"DOSSIER",styleYallow);
        createCell(rowIndividu,1,"FICHE",styleYallow);
        createCell(rowIndividu,2,"INDIVIDU",stylePurple);
        createCell(rowIndividu,3,"RIB.IBAN",stylePurple);
        createCell(rowIndividu,4,"RIB.DOMICILIATION",stylePurple);
        createCell(rowIndividu,5,"RIB.INTITULECOMPTE",stylePurple);
        createCell(rowIndividu,6,"RIB.CODERIB",styleYallow);
        createCell(rowIndividu,7,"RIB.TYPRIB",styleYallow);
        createCell(rowIndividu,8,"TYPECODE",styleYallow);


    }

    private void writeDataRIB(List<Ajout> personList) {
        int rowCount = 1;
        CellStyle styleData = workbook.createCellStyle();
        XSSFFont fontData = workbook.createFont();

        fontData.setFontName("Calibri");
        fontData.setFontHeight(11);

        styleData.setFont(fontData);


        for (Ajout p : personList){

            List<RIB> ribList = ribRepository.findAllByIndividuOrderByCodeRibDesc(p.getMatricule());
            if(p.getMatricule()==10595)
            System.out.println(ribList.get(0).getRib());
            if (ribList.isEmpty()) {

                Row row = rib.createRow(rib.getLastRowNum()+1);
                createCell(row, 0, "1", styleData);
                createCell(row, 1, "RIB", styleData);
                createCell(row, 2, p.getMatricule(), styleData);
                createCell(row, 3, p.getRib(), styleData);
                createCell(row, 4, p.getBanque().toUpperCase(), styleData);
                createCell(row, 5, p.getDomiciliation().toUpperCase(), styleData);
                // a developper la partie coderib
                createCell(row, 6, 1, styleData);
                createCell(row, 7, "2", styleData);
                createCell(row, 8, "2", styleData);

            }else if (ribList.isEmpty()==false){
               /* if (!ribList.get(0).getRib().trim().equals(p.getRib().trim())){*/
                    Row row = rib.createRow(rib.getLastRowNum()+1);
                    Row rowNext = rib.createRow(rib.getLastRowNum()+1);
                    createCell(row, 0, "1", styleData);
                    createCell(row, 1, "RIB", styleData);
                    createCell(row, 2, ribList.get(0).getIndividu(), styleData);
                    createCell(row, 3, ribList.get(0).getRib(), styleData);
                    createCell(row, 4,ribList.get(0).getBanque().toUpperCase(), styleData);
                    createCell(row, 5, ribList.get(0).getDomiciliation().toUpperCase(), styleData);
                    // a developper la partie coderib
                    createCell(row, 6, ribList.get(0).getCodeRib(), styleData);
                    createCell(row, 7, "1", styleData);
                    createCell(row, 8, "2", styleData);

                    createCell(rowNext, 0, "1", styleData);
                    createCell(rowNext, 1, "RIB", styleData);
                    createCell(rowNext, 2, p.getMatricule(), styleData);
                    createCell(rowNext, 3, p.getRib(), styleData);
                    createCell(rowNext, 4, p.getBanque().toUpperCase(), styleData);
                    createCell(rowNext, 5, p.getDomiciliation().toUpperCase(), styleData);
                    // a developper la partie coderib
                    createCell(rowNext, 6, ribList.get(0).getCodeRib()+1, styleData);
                    createCell(rowNext, 7, "2", styleData);
                    createCell(rowNext, 8, "2", styleData);
                }
            }

       /* }*/

    }

    private void writeHeaderLineContrat(){

        contrat = workbook.createSheet("Contrat");

        Row rowContrat = contrat.createRow(0);

        CellStyle styleYallow = workbook.createCellStyle();

        CellStyle styleBlue = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setFontName("Calibri");
        font.setFontHeight(11);
        font.setBold(true);

        styleYallow.setFont(font);
        styleYallow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleYallow.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleBlue.setFont(font);
        styleBlue.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        styleBlue.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(rowContrat,0,"FICHE",styleYallow);

        createCell(rowContrat,1,"INDIVIDU",styleBlue);
        createCell(rowContrat,2,"CONTRAT",styleBlue);
        createCell(rowContrat,3,"ETABLISSEMENT",styleYallow);
        createCell(rowContrat,4,"CONTRAT.ACTIF",styleYallow);
        createCell(rowContrat,5,"CONTRAT.CODENATURE",styleYallow);
        createCell(rowContrat,6,"CONTRAT.CODEPROFIL",styleBlue);
        createCell(rowContrat,7,"CONTRAT.PERIODICITE",styleYallow);
        createCell(rowContrat,8,"CONTRAT.MODEREGLEMENT",styleYallow);
        createCell(rowContrat,9,"CONTRAT.EMPLOI",styleBlue);
        createCell(rowContrat,10,"CONTRAT.CODEENTREE",styleYallow);
        createCell(rowContrat,11,"CONTRAT.TYPECONTRAT",styleYallow);
        createCell(rowContrat,12,"CONTRAT.DATEENTREE",styleBlue);
        createCell(rowContrat,13,"CONTRAT.DATEANCIENNETE",styleBlue);
        createCell(rowContrat,14,"CONTRAT.EMBAUCHEDT",styleBlue);
        createCell(rowContrat,15,"CONTRAT.LOCALISATION",styleBlue);


    }

    private void writeDataContrat(List<Ajout> personList){

        int rowCount = 1;
        CellStyle styleData = workbook.createCellStyle();
        XSSFFont fontData = workbook.createFont();



        fontData.setFontName("Calibri");
        fontData.setFontHeight(11);

        styleData.setFont(fontData);

        for (Ajout p : personList){


            List<Contrat> contratList = contratRepository.findAllByMatriculeOrderByNumcontratDesc(p.getMatricule());
            List<Contrat> contratsDate = contratRepository.findAllByMatriculeAndDateembaucheOrderByNumcontratDesc(p.getMatricule(),p.getDateentree());



            if (contratList.isEmpty()){
                Row row= contrat.createRow(rowCount++);
                createCell(row,0,"CONTRAT",styleData);
                createCell(row,1,FormatterMatricule(p.getMatricule()),styleData);
                createCell(row,2,1,styleData);
                createCell(row,3,"1",styleData);
                createCell(row,4,"2",styleData);
                createCell(row,5,"sal",styleData);

                if (p.getProfile().toLowerCase().equals("mensuel") || p.getProfile().equals("1") || p.getProfile().equals("1.0")){
                    createCell(row,6,"1",styleData);
                }else if (p.getProfile().toLowerCase().equals("horaire") || p.getProfile().equals("2") || p.getProfile().equals("2.0")){
                    createCell(row,6,"2",styleData);
                }

                createCell(row,7,"4",styleData);
                createCell(row,8,p.getModeregl().substring(0,1).toUpperCase(),styleData);

                createCell(row,9,p.getFonction(),styleData);
                createCell(row,10,"emb",styleData);
                createCell(row,11,p.getTypecontrat().toLowerCase(),styleData);


                createCell(row,12,dateFormatter.format( p.getDateentree()),styleData);


                createCell(row,13,dateFormatter.format(p.getDateentree()),styleData);
                createCell(row,14,dateFormatter.format(p.getDateentree()),styleData);
                createCell(row,15,p.getCodechantier(),styleData);

            }
            if (!contratList.isEmpty()){
                if (contratsDate.isEmpty()) {
                    Row row= contrat.createRow(rowCount++);
                    createCell(row,0,"CONTRAT",styleData);
                    createCell(row,1,FormatterMatricule(p.getMatricule()),styleData);
                    createCell(row, 2, contratList.get(0).getNumcontrat() + 1, styleData);
                    createCell(row,3,"1",styleData);
                    createCell(row,4,"2",styleData);
                    createCell(row,5,"sal",styleData);

                    if (p.getProfile().toLowerCase().equals("mensuel") || p.getProfile().equals("1") || p.getProfile().equals("1.0")){
                        createCell(row,6,"1",styleData);
                    }else if (p.getProfile().toLowerCase().equals("horaire") || p.getProfile().equals("2") || p.getProfile().equals("2.0")){
                        createCell(row,6,"2",styleData);
                    }

                    createCell(row,7,"4",styleData);
                    createCell(row,8,p.getModeregl().substring(0,1).toUpperCase(),styleData);

                    createCell(row,9,p.getFonction(),styleData);
                    createCell(row,10,"emb",styleData);
                    createCell(row,11,p.getTypecontrat().toLowerCase(),styleData);


                    createCell(row,12,dateFormatter.format( p.getDateentree()),styleData);


                    createCell(row,13,dateFormatter.format(p.getDateentree()),styleData);
                    createCell(row,14,dateFormatter.format(p.getDateentree()),styleData);
                    createCell(row,15,p.getCodechantier(),styleData);

                }
            }







        }
    }

    private void writeHeaderLineEnfants(){

        enfants = workbook.createSheet("Enfants");

        Row rowenfants = enfants.createRow(0);

        CellStyle stylePurple = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setFontName("Calibri");
        font.setFontHeight(11);
        font.setBold(true);


        stylePurple.setFont(font);
        stylePurple.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
        stylePurple.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(rowenfants,0,"DOSSIER",stylePurple);

        createCell(rowenfants,1,"FICHE",stylePurple);
        createCell(rowenfants,2,"INDIVIDU",stylePurple);
        createCell(rowenfants,3,"ENFANT.NOMENFANT",stylePurple);
        createCell(rowenfants,4,"ENFANT.PRENOM",stylePurple);
        createCell(rowenfants,5,"ENFANT.DATENAISSANCE",stylePurple);
        createCell(rowenfants,6,"ENFANT.SEXE",stylePurple);
        createCell(rowenfants,7,"ENFANT.ENFANTACHARGE",stylePurple);


    }

    private void writeDataEnfants(List<Ajout> personList){

        int rowCount = 1;
        CellStyle styleData = workbook.createCellStyle();
        XSSFFont fontData = workbook.createFont();

        fontData.setFontName("Calibri");
        fontData.setFontHeight(11);

        styleData.setFont(fontData);


        for (Ajout p : personList){

            Integer individuCount = individuRepository.countByIndividu(p.getMatricule());

            if (individuCount==0) {

                for (int i = 1; i <= p.getPcharge(); i++) {
                    Row row = enfants.createRow(rowCount++);
                    createCell(row, 0, "1", styleData);
                    createCell(row, 1, "ENFANT", styleData);
                    createCell(row, 2, FormatterMatricule(p.getMatricule()), styleData);
                    createCell(row, 3, p.getNom(), styleData);
                    createCell(row, 4, "ENFANT"+i, styleData);
                    createCell(row, 5, dateFormatter.format(p.getDatenaissance()), styleData);
                    if (i == 1) {
                        createCell(row, 6, "2", styleData);
                    } else
                        createCell(row, 6, "1", styleData);

                    createCell(row, 7, "2", styleData);
                }

            }
        }
    }

    private void writeHeaderLineElementvariable(){

        elementvariable = workbook.createSheet("Element_variables");

        Row rowIndividu = elementvariable.createRow(0);

        CellStyle styleTurquoise = workbook.createCellStyle();
        CellStyle styleOrange = workbook.createCellStyle();
        CellStyle styleGreen = workbook.createCellStyle();
        CellStyle styleWhite = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setFontName("Calibri");
        font.setFontHeight(11);
        font.setBold(true);

        styleWhite.setFont(font);
        styleWhite.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        styleWhite.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleOrange.setFont(font);
        styleOrange.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        styleOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleGreen.setFont(font);
        styleGreen.setFillForegroundColor(IndexedColors.LIME.getIndex());
        styleGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleTurquoise.setFont(font);
        styleTurquoise.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
        styleTurquoise.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(rowIndividu,0,"CE2",styleWhite);

        createCell(rowIndividu,1,"DOSSIER",styleTurquoise);
        createCell(rowIndividu,2,"ETABLISSEMENT",styleTurquoise);
        createCell(rowIndividu,3,"INDIVIDU",styleTurquoise);
        createCell(rowIndividu,4,"CORDRE",styleTurquoise);
        createCell(rowIndividu,5,"RUBRIQUE",styleTurquoise);
        createCell(rowIndividu,6,"LIBELLE",styleTurquoise);
        createCell(rowIndividu,7,"DateDebut",styleOrange);
        createCell(rowIndividu,8,"DateFin",styleOrange);
        createCell(rowIndividu,9,"Nombre",styleGreen);
        createCell(rowIndividu,10,"MONTANTSALARIAL",styleGreen);
        createCell(rowIndividu,11,"AXE_2",styleGreen);



    }

    private void writeDataElementvariable(List<OperationCaisse> operationCaisseList){

        int rowCount = 1;
        CellStyle styleData = workbook.createCellStyle();
        XSSFFont fontData = workbook.createFont();

        fontData.setFontName("Calibri");
        fontData.setFontHeight(11);

        styleData.setFont(fontData);
        Calendar cal = new GregorianCalendar();



        for (OperationCaisse op : operationCaisseList){

            List<Contrat> contratList = contratRepository.findAllByMatriculeOrderByNumcontratDesc(op.getIndividu().getIndividu());

            int month= op.getDateOper().getMonth();
            cal.set(cal.get(Calendar.YEAR), month, 1); //note that the month in Calendar goes from 0-11
            int month1= month+1;
            int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            Row row= elementvariable.createRow(rowCount++);

            createCell(row,0,"T",styleData);
            createCell(row,1,"1",styleData);
            createCell(row,2,"1",styleData);
            createCell(row,3,op.getIndividu().getIndividu(),styleData);
            createCell(row,4,"1",styleData);
            createCell(row,5,op.getMotif().getCodeMotif(),styleData);
            createCell(row,6,op.getMotif().getDesc_motif(),styleData);

            if (contratList.get(0).getProfilecode().trim().equals("1")){
                if (month1<10){
                    createCell(row, 7, "01/0" + month1 + "/" + (1900+op.getDateOper().getYear()), styleData);
                    createCell(row, 8, max + "/0" + month1 + "/" + (1900+op.getDateOper().getYear()), styleData);
                }
                if (month1>=10){
                    createCell(row, 7, "01/" + month1 + "/" + (1900+op.getDateOper().getYear()), styleData);
                    createCell(row, 8, max + "/" + month1 + "/" +(1900+op.getDateOper().getYear()), styleData);
                }
            }else if (contratList.get(0).getProfilecode().trim().equals("2")) {
                if (month1<10){
                    createCell(row, 7, "01/0" + month1 + "/" + (1900+op.getDateOper().getYear()), styleData);
                    createCell(row, 8, max + "/0" + month1 + "/" + (1900+op.getDateOper().getYear()), styleData);
                }
                if (month1>=10){
                    createCell(row, 7, "01/" + month1 + "/" + (1900+op.getDateOper().getYear()), styleData);
                    createCell(row, 8, max + "/" + month1 + "/" + (1900+op.getDateOper().getYear()), styleData);
                }
            }
            if (op.getNumJrs()==null){
                createCell(row, 9, "", styleData);
            }
            if (op.getNumJrs()!=null){
                createCell(row, 11, op.getNumJrs(), styleData);
            }

            createCell(row, 10, op.getMontant(), styleData);

            if (op.getAffaire()==null){
                createCell(row, 11, "", styleData);
            }
            if (op.getAffaire()!=null){
                createCell(row, 11, op.getAffaire().getCode(), styleData);
            }


        }
    }

    private void writeHeaderLineConstantes(){

        constantes = workbook.createSheet("Constantes");

        Row rowIndividu = constantes.createRow(0);

        CellStyle styleTurquoise = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();

        font.setFontName("Calibri");
        font.setFontHeight(11);
        font.setBold(true);



        styleTurquoise.setFont(font);
        styleTurquoise.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
        styleTurquoise.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(rowIndividu,0,"CONTRAT",styleTurquoise);

        createCell(rowIndividu,1,"INDIVIDU",styleTurquoise);
        createCell(rowIndividu,2,"CORDRE",styleTurquoise);
        createCell(rowIndividu,3,"CONSTANTE",styleTurquoise);
        createCell(rowIndividu,4,"CSTTYP",styleTurquoise);
        createCell(rowIndividu,5,"CSTDATATYP",styleTurquoise);
        createCell(rowIndividu,6,"EFFETDT",styleTurquoise);
        createCell(rowIndividu,7,"EFFETFDT",styleTurquoise);
       
        // Row row = constantes.createRow(1); 
      //  row.createCell(7).setCellValue(dateFormatter.format("31/12/9999"));


    }

    private void writeDataConstantes(List<Ajout> personList){

        int rowCount = 1;
        CellStyle styleData = workbook.createCellStyle();
        XSSFFont fontData = workbook.createFont();

        fontData.setFontName("Calibri");
        fontData.setFontHeight(11);

        styleData.setFont(fontData);


        for (Ajout p : personList) {

            List<Contrat> contratList = contratRepository.findAllByMatriculeOrderByNumcontratDesc(p.getMatricule());
            List<Contrat> contratsDate = contratRepository.findAllByMatriculeAndDateembaucheOrderByNumcontratDesc(p.getMatricule(), p.getDateentree());

            if (p.getProfile().toLowerCase().equals("horaire") || p.getProfile().equals("2") || p.getProfile().equals("2.0")) {
                if (contratList.isEmpty()) {
                    Row row = constantes.createRow(rowCount++);
                    createCell(row, 0, 1, styleData);
                    createCell(row, 1, FormatterMatricule(p.getMatricule()), styleData);
                    createCell(row, 2, "1", styleData);
                    if (p.getProfile().toLowerCase().equals("mensuel") || p.getProfile().equals("1") || p.getProfile().equals("1.0")) {
                        createCell(row, 3, "salaire", styleData);
                    } else if (p.getProfile().toLowerCase().equals("horaire") || p.getProfile().equals("2") || p.getProfile().equals("2.0")) {
                        createCell(row, 3, "taux_horaire", styleData);
                    }

                    createCell(row, 4, "1", styleData);
                    createCell(row, 5, p.getSalaire(), styleData);
                    createCell(row, 6, dateFormatter.format(p.getDateentree()), styleData);
                    createCell(row, 7, "31/12/9999", styleData);

                }
                if (!contratList.isEmpty()) {

                    if (contratsDate.isEmpty()) {
                        Row row = constantes.createRow(rowCount++);
                        createCell(row, 0, contratList.get(0).getNumcontrat() + 1, styleData);
                        createCell(row, 1, FormatterMatricule(p.getMatricule()), styleData);
                        createCell(row, 2, "1", styleData);
                        if (p.getProfile().toLowerCase().equals("mensuel") || p.getProfile().equals("1") || p.getProfile().equals("1.0")) {
                            createCell(row, 3, "salaire", styleData);
                        } else if (p.getProfile().toLowerCase().equals("horaire") || p.getProfile().equals("2") || p.getProfile().equals("2.0")) {
                            createCell(row, 3, "taux_horaire", styleData);
                        }

                        createCell(row, 4, "1", styleData);
                        createCell(row, 5, p.getSalaire(), styleData);
                        createCell(row, 6, dateFormatter.format(p.getDateentree()), styleData);
                        createCell(row, 7, "31/12/9999", styleData);
                    }
                }

                System.out.println("RowCount"+rowCount);
            }
        }
    }

    private void writeHeaderLineCPAbsences(){

        cpabsences = workbook.createSheet("CP et absences");

        Row rowIndividu = cpabsences.createRow(0);

        CellStyle styleTurquoise = workbook.createCellStyle();
        CellStyle styleOrange = workbook.createCellStyle();
        CellStyle styleGreen = workbook.createCellStyle();
        CellStyle styleWhite = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setFontName("Calibri");
        font.setFontHeight(11);
        font.setBold(true);

        styleWhite.setFont(font);
        styleWhite.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        styleWhite.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleOrange.setFont(font);
        styleOrange.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        styleOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleGreen.setFont(font);
        styleGreen.setFillForegroundColor(IndexedColors.LIME.getIndex());
        styleGreen.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleTurquoise.setFont(font);
        styleTurquoise.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
        styleTurquoise.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(rowIndividu,0,"CE2",styleWhite);

        createCell(rowIndividu,1,"DOSSIER",styleTurquoise);
        createCell(rowIndividu,2,"ETABLISSEMENT",styleTurquoise);
        createCell(rowIndividu,3,"INDIVIDU",styleTurquoise);
        createCell(rowIndividu,4,"CORDRE",styleTurquoise);
        createCell(rowIndividu,5,"RUBRIQUE",styleTurquoise);
        createCell(rowIndividu,6,"LIBELLE",styleTurquoise);
        createCell(rowIndividu,7,"DateDebut",styleOrange);
        createCell(rowIndividu,8,"DateFin",styleOrange);
        createCell(rowIndividu,9,"Nombre",styleGreen);


    }

    private void writeHeaderLineEmploi(){

        emploi = workbook.createSheet("Emploi");

        Row rowEmploi = emploi.createRow(0);
        Row row3 = emploi.createRow(2);
        Row row6 = emploi.createRow(5);
        Row row8 = emploi.createRow(7);


        CellStyle styleGray = workbook.createCellStyle();
        CellStyle styleRed = workbook.createCellStyle();
        CellStyle styleLightOrange = workbook.createCellStyle();
        CellStyle styleOrange = workbook.createCellStyle();
        CellStyle styleWhite = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setFontName("Calibri");
        font.setFontHeight(11);
        font.setBold(true);

        styleWhite.setFont(font);
        styleWhite.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        styleWhite.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleGray.setFont(font);
        styleGray.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        styleGray.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleRed.setFont(font);
        styleRed.setFillForegroundColor(IndexedColors.RED.getIndex());
        styleRed.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleLightOrange.setFont(font);
        styleLightOrange.setFillForegroundColor(IndexedColors.TAN.getIndex());
        styleLightOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleOrange.setFont(font);
        styleOrange.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
        styleOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);


        createCell(rowEmploi,0,"Table_Emploi - Table des emplois",styleGray);

        createCell(row3,0,"Identifiant",styleRed);
        createCell(row3,1,"6",styleWhite);

        createCell(row6,0,"Champs clé",styleLightOrange);
        createCell(row6,1,"",styleLightOrange);
        createCell(row6,2,"Données",styleLightOrange);
        createCell(row6,11,"Anomalies",styleRed);

        createCell(row8,0,"DOSSIER",styleLightOrange);
        createCell(row8,1,"EMPLOI",styleLightOrange);
        createCell(row8,2,"LIBELLE_TABLE_EMPLOI",styleOrange);
        createCell(row8,3,"TYPEPAIE",styleOrange);
        createCell(row8,4,"CODEINSEEEMPLOI",styleOrange);
        createCell(row8,5,"QUALIFICATION",styleOrange);
        createCell(row8,6,"CATEGORIESOCIOPROFESSIONNELLE",styleOrange);
        createCell(row8,7,"NIVEAU",styleOrange);
        createCell(row8,8,"ECHELON",styleOrange);
        createCell(row8,9,"COEFFICIENT",styleOrange);
        createCell(row8,10,"POSITION",styleOrange);
        createCell(row8,11,"",styleRed);
    }

    private void writeHeaderLineSTC(){

        stc = workbook.createSheet("STC");

        Row rowSTC = stc.createRow(0);

        CellStyle styleYallow = workbook.createCellStyle();

        CellStyle styleBlue = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setFontName("Calibri");
        font.setFontHeight(11);
        font.setBold(true);

        styleYallow.setFont(font);
        styleYallow.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        styleYallow.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleBlue.setFont(font);
        styleBlue.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        styleBlue.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(rowSTC,0,"FICHE",styleYallow);

        createCell(rowSTC,1,"INDIVIDU",styleBlue);
        createCell(rowSTC,2,"CONTRAT",styleBlue);
        createCell(rowSTC,3,"ETABLISSEMENT",styleYallow);
        createCell(rowSTC,4,"CONTRAT.ACTIF",styleYallow);
        createCell(rowSTC,5,"CONTRAT.CODEPROFIL",styleBlue);

        createCell(rowSTC,6,"CONTRAT.DATESORTIE",styleBlue);


    }

    private void writeDataSTC(List<STC> stcList){

        int rowCount = 1;
        CellStyle styleData = workbook.createCellStyle();
        XSSFFont fontData = workbook.createFont();

        fontData.setFontName("Calibri");
        fontData.setFontHeight(11);
        styleData.setFont(fontData);

        List<String> Profile = new ArrayList<>();
        Profile.add("STC");
        Profile.add("STCM");
        for (STC s : stcList){
            List<Contrat> contratList = contratRepository.findAllByMatriculeAndDatesortieAndProfilecodeNotIn(s.getIndividu().getIndividu(),s.getDatedepart(),Profile);
            List<Contrat> contratsnum = contratRepository.findAllByMatriculeOrderByNumcontratDesc(s.getIndividu().getIndividu());
            Integer opExist = operationCaisseRepository.countAllByIndividu(s.getIndividu());



            if (contratList.isEmpty()) {
                Row row = stc.createRow(rowCount++);
                createCell(row, 0, "CONTRAT", styleData);
                createCell(row, 1, s.getIndividu().getIndividu(), styleData);
                createCell(row, 2, contratsnum.get(0).getNumcontrat(), styleData);
                createCell(row, 3, "1", styleData);
                createCell(row, 4, "2", styleData);

                if (contratsnum.get(0).getProfilecode().equals("1")){
                    createCell(row, 5, "STCM", styleData);
                }
                if (contratsnum.get(0).getProfilecode().equals("2")){
                    createCell(row, 5, "STC", styleData);
                }
                createCell(row, 6,dateFormatter.format(s.getDatedepart()), styleData);

            }
        }
    }
    private void writeDataSTCVide(){

        int rowCount = 1;
        CellStyle styleData = workbook.createCellStyle();
        XSSFFont fontData = workbook.createFont();

        fontData.setFontName("Calibri");
        fontData.setFontHeight(11);
        styleData.setFont(fontData);

        Row row = stc.createRow(rowCount);

        createCell(row, 0, "Aucun STC à exporter", styleData);
    }

    private void writeHeaderLineAvance(){

        avance = workbook.createSheet("Avance");

        Row rowSTC = avance.createRow(0);

        CellStyle styleturkoi = workbook.createCellStyle();

        CellStyle styleOrange = workbook.createCellStyle();

        XSSFFont font = workbook.createFont();

        font.setFontName("Calibri");
        font.setFontHeight(11);
        font.setBold(true);

        styleturkoi.setFont(font);
        styleturkoi.setFillForegroundColor(IndexedColors.TURQUOISE.getIndex());
        styleturkoi.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        styleOrange.setFont(font);
        styleOrange.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        styleOrange.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        createCell(rowSTC,0,"INDIVIDU",styleturkoi);
        createCell(rowSTC,1,"AXE_2",styleturkoi);
        createCell(rowSTC,2,"AXE_3",styleturkoi);
        createCell(rowSTC,3,"DateDebut",styleOrange);
        createCell(rowSTC,4,"DateFin",styleOrange);
        createCell(rowSTC,5,"CE2",styleturkoi);

        createCell(rowSTC,6,"DOSSIER",styleturkoi);
        createCell(rowSTC,7,"ETABLISSEMENT",styleturkoi);

        createCell(rowSTC,8,"CORDRE",styleturkoi);
        createCell(rowSTC,9,"RUBRIQUE",styleturkoi);

        createCell(rowSTC,10,"LIBELLE",styleturkoi);
        createCell(rowSTC,11,"Nombre",styleturkoi);
        createCell(rowSTC,12,"Montant_salarial",styleturkoi);



    }

    private void writeDataAvances(List<AvanceDTO> avanceDTOS){

        int rowCount = 1;
        CellStyle styleData = workbook.createCellStyle();
        XSSFFont fontData = workbook.createFont();

        fontData.setFontName("Calibri");
        fontData.setFontHeight(11);

        styleData.setFont(fontData);
        Calendar cal = new GregorianCalendar();

        if (!avanceDTOS.isEmpty()){
            for (AvanceDTO av : avanceDTOS){

                int month= av.getDateAvance().getMonth();
                cal.set(cal.get(Calendar.YEAR), month, 1); //note that the month in Calendar goes from 0-11
                int month1= month+1;
                int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                Row row= avance.createRow(rowCount++);
                createCell(row,0,av.getMatricule(),styleData);
                createCell(row,1,av.getChantier(),styleData);
                createCell(row,2,"",styleData);
                if (month1<10){
                    createCell(row, 3, "01/0" + month1 + "/" + (1900+av.getDateAvance().getYear()), styleData);
                    createCell(row, 4, max + "/0" + month1 + "/" + (1900+av.getDateAvance().getYear()), styleData);
                }
                if (month1>=10){
                    createCell(row, 3, "01/" + month1 + "/" + (1900+av.getDateAvance().getYear()), styleData);
                    createCell(row, 4, max + "/" + month1 + "/" +(1900+av.getDateAvance().getYear()), styleData);
                }
                createCell(row,5,"T",styleData);
                createCell(row,6,"1",styleData);
                createCell(row,7,"1",styleData);
                createCell(row,8,"",styleData);
                createCell(row,9,"Avance_sur_salaire",styleData);
                createCell(row,10,"Avance sur Salaire virement",styleData);
                createCell(row,11,"",styleData);
                createCell(row,12,av.getMontant(),styleData);



            }
        }



    }


    public void export(HttpServletResponse response,List<Ajout> personList) throws IOException {
        writeHeaderLines();

        writeDataIndividu(personList);
        writeDataContrat(personList);
        writeDataEnfants(personList);
        writeDataConstantes(personList);
        writeDataRIB(personList);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }

    public void exportEV(HttpServletResponse response,List<OperationCaisse> operationCaisseList) throws IOException {
        workbook = new XSSFWorkbook();
        writeHeaderLineElementvariable();
        writeDataElementvariable(operationCaisseList);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }

    public void exportSTC(HttpServletResponse response,List<STC> stcList) throws IOException {
        workbook = new XSSFWorkbook();
        writeHeaderLineSTC();

        if (stcList.isEmpty()){
            writeDataSTCVide();
        }else {
            writeDataSTC(stcList);
        }
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }

    public void exportCanvas(HttpServletResponse response) throws  IOException{
        Canvas_importer();
        ServletOutputStream outputStreamimporter = response.getOutputStream();
        workbook.write(outputStreamimporter);
        workbook.close();
        outputStreamimporter.close();

    }

    public void exportAvance(HttpServletResponse response,List<AvanceDTO> avanceDTOS) throws IOException {
        workbook = new XSSFWorkbook();
        writeHeaderLineAvance();
        writeDataAvances(avanceDTOS);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();

        outputStream.close();

    }



}
