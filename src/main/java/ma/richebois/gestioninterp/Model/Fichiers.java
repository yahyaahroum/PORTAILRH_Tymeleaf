package ma.richebois.gestioninterp.Model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fichiers extends Auditable<String> implements Serializable{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private long groupId;
	private String name;
}

