package ma.richebois.gestioninterp.Model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Auditable<U> {

    @CreatedBy
    @Column(name = "Creer_par")
    private U createdBy;

    @CreatedDate
    @Column(name = "Creer_le")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @LastModifiedBy
    @Column(name = "Modifier_par")
    private U lastModifiedBy;

    @LastModifiedDate
    @Column(name = "Modifier_le")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;

}
