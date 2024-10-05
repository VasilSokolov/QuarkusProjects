package repository;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import com.speedment.jpastreamer.application.JPAStreamer;
import com.speedment.jpastreamer.projection.Projection;
import com.speedment.jpastreamer.streamconfiguration.StreamConfiguration;
import enums.ReportType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import model.Film;
import model.Film$;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ApplicationScoped
public class FilmRepository {

    private static final int PAGE_SIZE = 20;
    public static final String REPORT_FILE_PATH = "/reports";

    @Inject
    JPAStreamer jpaStreamer;

    public Optional<Film> getFilm(Integer filmId){
        return jpaStreamer.stream(Film.class)
                .filter(Film$.id.equal(filmId))
                .findFirst();
    }

    public Stream<Film> getFilmsByMinLength(Integer minLength){
        return jpaStreamer.stream(Film.class)
                .filter(Film$.length.greaterThan(minLength))
                .sorted(Film$.length);
    }

    public Stream<Film> getFilms(){
        return jpaStreamer.stream(Film.class)
                .sorted(Film$.length);
    }

    public Stream<Film> getFilmsById(Integer filmId){
        return jpaStreamer.stream(Film.class)
                .filter(Film$.id.equal(filmId))
                .sorted(Film$.length);
    }

    public Stream<Film> paged(long page, int minLength) {
        return jpaStreamer.stream(Projection.select(
                        Film$.id,
                        Film$.title,
                        Film$.length))
                .filter(Film$.length.greaterThan(minLength))
                .sorted(Film$.length)
                .skip(page * PAGE_SIZE)
                .limit(PAGE_SIZE);
    }

//    public Stream<Film> paged(long page, int minLength) {
//        return jpaStreamer.stream(Film.class)
//                .filter(Film$.length.greaterThan(minLength))
//                .sorted(Film$.length)
//                .skip(page * PAGE_SIZE)
//                .limit(PAGE_SIZE);
//    }

    public Stream<Film> actors(String startsWith, int minLength) {
        final StreamConfiguration<Film> sc = StreamConfiguration.of(Film.class)
                .joining(Film$.actors);
        return jpaStreamer.stream(sc)
                .filter(Film$.title.startsWith(startsWith).and(Film$.length.greaterThan(minLength)))
                .sorted(Film$.length.reversed());
    }

    @Transactional
    public void updateRentalRate(int minLength, BigDecimal rentalRate){
        jpaStreamer.stream(Film.class)
                .filter(Film$.length.greaterThan(minLength))
                .forEach(f -> {
                    f.setRentalRate(rentalRate);
                });
    }

    public void getReportOfFilmsById(Integer filmId, String name, ReportType reportType) {
        Stream<Film> films;
        if (filmId == null) {
            films = getFilms();
        } else {
            films = getFilmsById(filmId);
        }

        switch (reportType) {
            case CSV:
                writeDataAtOnce(REPORT_FILE_PATH, films, name);
                break;
            case PDF:
                try {
                    getPDF_ReportOfFilmsById(films, name);
                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case DEFAULT:
                System.out.println("Return pdf");
                break;

        }
    }

    public static void writeDataAtOnce(String filePath, Stream<Film> films, String fileName) {
        try {
            // first create file object for file placed at location
            // specified by filepath
            File file = getFile(filePath, fileName);

            // create FileWriter object with file as parameter
            FileWriter outputFile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputFile);

            // create a List which contains String array
            List<String[]> data = new ArrayList<>();
            films.forEach(i -> data.add(new String[] {
                    i.getId() != null ? i.getId().toString() : "0",
                    i.getTitle(),
                    i.getDescription()
            }));
            writer.writeAll(data);

            // closing writer connection
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Can't create csv!");
        }
    }

    private static File getFile(String filePath, String fileName) throws IOException {
        String directoryPathName = String.valueOf(Paths.get(Paths.get("").toRealPath().toString()));
        File directory = new File(directoryPathName, filePath);
        if (!directory.exists()){
            directory.mkdir();
        }

        File file = new File(directory, fileName);
        return file;
    }

    public void getPDF_ReportOfFilmsById(Stream<Film> films, String pdfName) throws DocumentException, IOException {
        Document document = new Document();
        File file = getFile(REPORT_FILE_PATH, pdfName);
//        PdfWriter.getInstance(document, new FileOutputStream("iTextTable.pdf"));
        PdfWriter.getInstance(document, new FileOutputStream(file.getAbsolutePath()));
        document.open();
        PdfPTable table = new PdfPTable(3);
        addTableHeader(table);
        addRows(table, films);
//        addCustomRows(table);

        document.add(table);
        document.close();
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("ID", "TITLE", "DESCRIPTION")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }
    private void addRows(PdfPTable table, Stream<Film> films) {
        films.forEach(i -> {
            table.addCell( i.getId() != null ? i.getId().toString() : "0");
            table.addCell( i.getTitle());
            table.addCell( i.getDescription());
            });
//        table.addCell("row 1, col 2");
//        table.addCell("row 1, col 3");
    }

//    private void addCustomRows(PdfPTable table)
//            throws URISyntaxException, BadElementException, IOException {
//        Path path = Paths.get(ClassLoader.getSystemResource("Java_logo.png").toURI());
//        Image img = Image.getInstance(path.toAbsolutePath().toString());
//        img.scalePercent(10);
//
//        PdfPCell imageCell = new PdfPCell(img);
//        table.addCell(imageCell);
//
//        PdfPCell horizontalAlignCell = new PdfPCell(new Phrase("row 2, col 2"));
//        horizontalAlignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        table.addCell(horizontalAlignCell);
//
//        PdfPCell verticalAlignCell = new PdfPCell(new Phrase("row 2, col 3"));
//        verticalAlignCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
//        table.addCell(verticalAlignCell);
//    }
}
