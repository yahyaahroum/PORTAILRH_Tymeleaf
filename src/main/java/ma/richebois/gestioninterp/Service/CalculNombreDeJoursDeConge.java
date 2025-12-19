package ma.richebois.gestioninterp.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ma.richebois.gestioninterp.DTO.CongeDTO;


public class CalculNombreDeJoursDeConge {


	public static CongeDTO CalculNombreDeJours(CongeDTO congeDTO, Date dateDebutP, Date dateFinP)
	{
		Set<String> joursFeries = new HashSet<>();
		joursFeries.add("01-01");  //nouvel an
		joursFeries.add("01-11");  //anniversaire de l'Indépendance
		joursFeries.add("01-14");  //Nouvel an Amazigh
		joursFeries.add("05-01");  //fête du Travail
		joursFeries.add("07-30");  //fête du Trône
		joursFeries.add("08-14");  //commémoration de l'allégeance de l'oued Eddahab
		joursFeries.add("08-20");  //anniversaire de la révolution, du roi et du peuple
		joursFeries.add("08-21");  //anniversaire du roi Mohammed VI
		joursFeries.add("10-31");  //Aïd Al Wahda
		joursFeries.add("11-06");  //Marche Verte
		joursFeries.add("11-18");  //Fête de l'Indépendance
		String jourMois;
		Calendar calendarDebut = Calendar.getInstance();
		calendarDebut.setTime(dateDebutP);

		Calendar calendarFin = Calendar.getInstance();
		calendarFin.setTime(dateFinP);

		LocalDate dateDebut=LocalDate.of(calendarDebut.get(calendarDebut.YEAR),calendarDebut.get(calendarDebut.MONTH)+1, calendarDebut.get(calendarDebut.DAY_OF_MONTH));
		LocalDate dateFin = LocalDate.of(calendarFin.get(calendarFin.YEAR),calendarFin.get(calendarFin.MONTH)+1, calendarFin.get(calendarFin.DAY_OF_MONTH));

		int nombreJoursConges = 0;
		LocalDate dateCourante = dateDebut;

		while (!dateCourante.isAfter(dateFin)) {
			jourMois = String.format("%02d-%02d", dateCourante.getMonthValue(), dateCourante.getDayOfMonth());

			if (!dateCourante.getDayOfWeek().equals(DayOfWeek.SUNDAY) &&
					!joursFeries.contains(jourMois)) {
				nombreJoursConges++;
			}
			dateCourante = dateCourante.plusDays(1);
		}
		congeDTO.setNbrJour(nombreJoursConges);
		return congeDTO;
	}

}