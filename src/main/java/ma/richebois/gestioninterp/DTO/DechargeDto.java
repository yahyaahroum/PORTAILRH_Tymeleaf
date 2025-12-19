package ma.richebois.gestioninterp.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DechargeDto {

    private Date dateDecharge;
    private long individu;
    private String statut;
    private long chantier;
    private List<DechargeDetDTO> dechargeDetList;

    @Override
    public String toString() {
        return "DechargeDto{" +
                "dateDecharge=" + dateDecharge +
                ", individu=" + individu +
                ", statut='" + statut + '\'' +
                ", chantier=" + chantier +
                ", dechargeDetList=" + dechargeDetList +
                '}';
    }
}
