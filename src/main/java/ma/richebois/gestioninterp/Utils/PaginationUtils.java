package ma.richebois.gestioninterp.Utils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaginationUtils {

    /**
     * Génère une liste de numéros de pages à afficher de manière compacte.
     *
     * @param currentPage        page courante (1-based)
     * @param totalPages         nombre total de pages
     * @param maxVisiblePages    nombre maximum de pages à afficher (ex: 6)
     * @return liste des numéros de page à afficher dans la pagination
     */
    public static List<Integer> getCompactPageNumbers(int currentPage, int totalPages, int maxVisiblePages) {
        int startPage = Math.max(1, currentPage - maxVisiblePages / 2);
        int endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
        if ((endPage - startPage + 1) < maxVisiblePages) {
            startPage = Math.max(1, endPage - maxVisiblePages + 1);
        }
        return IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
    }
}