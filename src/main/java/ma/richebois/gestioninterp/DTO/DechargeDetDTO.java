package ma.richebois.gestioninterp.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DechargeDetDTO {

    private int quantite;
    private String marque;
    private String model;
    private long materiel;

    @Override
    public String toString() {
        return "DechargeDetDTO{" +
                "quantite=" + quantite +
                ", marque='" + marque + '\'' +
                ", Model='" + model + '\'' +
                ", materiel=" + materiel +
                '}';
    }
}
