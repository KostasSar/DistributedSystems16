package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ExternalCarServlet
 */
@WebServlet("/ExternalCarServlet")
public class ExternalCarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Allow access only if cookie exists
		Cookie[] cookies = request.getCookies();
		String userName = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("userName")) {
					userName = cookie.getValue();
				}
			}
		}

		if (userName == null) {
			response.sendRedirect("Login.html");
		} else {

			Connection con = (Connection) getServletContext().getAttribute("DBConnection");
			PreparedStatement ps = null;
			ResultSet rs = null;

			String condition = request.getParameter("condition");
			String hour = request.getParameter("hour");
			int status = 0;

			// ΟΙ ΜΕΤΑΒΛΗΤΕΣ ΠΟΥ ΜΑΣ ΛΕΙΠΟΥΝ
			int store; // Κωδικος μαγαζιου
			float distance; // Αποσταση
			Date appointment_date; // Ημερομηνια και ωρα
			Float cost; // Κοστος μεταφορας
			String pickuplocation; // Σημειο παραλαβης
			String userTRN = null;
			HttpSession session = request.getSession(true);
			userTRN = (String) session.getAttribute(userTRN);
			Long ctrn = Long.parseLong(userTRN);

			try {
				// Store appointment details in database
				ps = con.prepareStatement("INSERT INTO WebAppointments VALUES (?");
				ps.setLong(1, ctrn);
				ps.setDate(2, appointment_date);
				ps.setInt(3, store);
				ps.setFloat(4, distance);
				ps.setFloat(5, cost);
				ps.setInt(6, status);
				ps.setString(7, pickuplocation);

				// If update is successful redirect user to the appropriate page

				RequestDispatcher rd = getServletContext().getRequestDispatcher("/ExternalConfirmation.html");
				PrintWriter out = response.getWriter();
				out.println("<html><body>");
				out.println("<h1>Your appointment has been succesfully booked!</h1>");
				out.println("</body></html>");
				rd.include(request, response);

			}
			// If updating database fails inform user
			catch (SQLException e) {
				e.printStackTrace();

				RequestDispatcher rd = getServletContext().getRequestDispatcher("/ExternalCarForm.html");
				PrintWriter out = response.getWriter();
				out.println(
						"<font color=red> We were unable to save your appointment.Please try again.</font>");
				rd.include(request, response);
			}
		}
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExternalCarServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

}
