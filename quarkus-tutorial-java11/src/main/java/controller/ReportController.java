package controller;

import enums.ReportType;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import repository.FilmRepository;

@Path("/report")
public class ReportController {
    public static final String CSV_NAME = "csvReport.csv";
    public static final String PDF_NAME = "pdfReport.pdf";

    @Inject
    FilmRepository filmRepository;

    @GET
    @Path("/{filmId}/csv")
    @Produces(MediaType.TEXT_PLAIN)
    public String csvReportsGenerated(Integer filmId) {
        filmRepository.getReportOfFilmsById(filmId, CSV_NAME, ReportType.CSV);
        return "Create csv";
    }
//    @DefaultValue("true")
    @GET
    @Path("/{filmId}/pdf")
    @Produces(MediaType.TEXT_PLAIN)
    public String pdfReportsGenerated(@QueryParam("filmId") Integer filmId) {
        System.out.println(filmId);
        filmRepository.getReportOfFilmsById(filmId, PDF_NAME, ReportType.PDF);
        return "Create pdf";
    }

    @GET
    @Path("/{filmId}/pdfFile")
    @Produces(MediaType.TEXT_PLAIN)
    public String pdfReportsFile(Integer filmId)  {
//        filmRepository.getReportOfFilmsById(filmId, PDF_NAME, ReportType.DEFAULT);
        return "PDF File";
    }
}
