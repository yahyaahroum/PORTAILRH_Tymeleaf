package ma.richebois.gestioninterp.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PointageArrayDto {
    List<PointageDTO> pointageDTOList;

    @Override
    public String toString() {
        return "PointageArrayDto{" +
                "pointageDTOList=" + pointageDTOList +
                '}';
    }
}
