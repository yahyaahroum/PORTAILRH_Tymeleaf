package ma.richebois.gestioninterp.Service;

import java.text.ParseException;
import java.util.Map;

public interface AcceuilService {

    public Integer getPersonByState(String State);
    public Float getStatistique(String state);
    public Integer getSTCByState(String state);
    public Float getStaticSTC(String state) throws ParseException;
    public int countAllPersonesInChantier();
    public int countPersonesPerDay() throws ParseException;

    public Map<String,Integer> charteEmbauche();

    public Integer getCongeByState(String state);
    public Float getStaticConge(String state) throws ParseException;

    public Integer getDemandeByState(String state);
    public Float getStaticDemande(String state) throws ParseException;

}
