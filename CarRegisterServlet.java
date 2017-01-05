
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CarResgisterServlet
 */
@WebServlet("/CarRegisterServlet")
public class CarRegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//Αllow access only if cookie exists
		Cookie[] cookies = request.getCookies();
		String userName = null;
		String department = null;

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("userName")) {
					userName = cookie.getValue();
				}
				if (cookie.getName().equals("userDepartment")) {
					department = cookie.getValue();
				}

			}
		}

		if (userName == null) {
			response.sendRedirect("Login.html");
		} else {

			// Create new session
			HttpSession session = request.getSession();

			// Receive data from form
			String carModel = request.getParameter("carmodel");
			String carPlate = request.getParameter("carplate");
			String carFuel = request.getParameter("fuel");
			String year = request.getParameter("caryear");

			// Check if received data is empty
			if (!carModel.isEmpty() && !carPlate.isEmpty() && !carFuel.isEmpty() && !year.isEmpty()) {

				int caryear = Integer.parseInt(year);
				int currentyear = Calendar.getInstance().get(Calendar.YEAR);

				// Check if car year is correct
				if (caryear >= 1900 && caryear <= currentyear) {

					// Check if inserted car plate number has the correct
					// pattern
					if (carPlate.length() == 8 && carPlate.matches("^[A-Z][A-Z][A-Z][-][0-9][0-9][0-9].$")) {

						// Get data from session
						String state = "Unchecked";
						Date date = java.sql.Date.valueOf(java.time.LocalDate.now());
						int store = Integer.parseInt(department);
						Long trn = Long.parseLong((String) request.getSession().getAttribute("customerTRN"));
						int entry = 2;
						Connection con = (Connection) getServletContext().getAttribute("DBConnection");
						PreparedStatement ps = null;
						PreparedStatement ps1 = null;
						ResultSet rs = null;
						ResultSet rs1 = null;

						try {

							// Get latest entry number
							ps = con.prepareStatement(" SELECT Entry FROM VEHICLE ORDER BY Entry DESC LIMIT 1");
							rs = ps.executeQuery();
							rs.next();
							entry = rs.getInt("Entry") + 1;

							// Check database for existing registration with
							// the
							// same car plate number
							ps = con.prepareStatement(" SELECT * FROM VEHICLE WHERE License_Plate=?");
							ps.setString(1, carPlate);
							rs1 = ps.executeQuery();

							if (!rs1.isBeforeFirst()) {

								// Update database
								ps1 = con.prepareStatement(
										"INSERT INTO VEHICLE  (Entry,Model,License_Plate,Fuel_Type,Release_Year,State,Owner_TRN,Store,Registration_Date) VALUES (?,?,?,?,?,?,?,?,?)");
								ps1.setInt(1, entry);
								ps1.setString(2, carModel);
								ps1.setString(3, carPlate);
								ps1.setString(4, carFuel);
								ps1.setInt(5, caryear);
								ps1.setString(6, state);
								ps1.setLong(7, trn);
								ps1.setInt(8, store);
								ps1.setDate(9, date);
								ps1.executeUpdate();

								// If update was successful
								// Input data is stored in session for later
								// use
								session.setAttribute("carName", carModel);
								session.setAttribute("carPlate", carPlate);
								session.setAttribute("carFuel", carFuel);

								// Inform user for successful update an
								// redirect
								// them to the next webpage
								RequestDispatcher rd = getServletContext().getRequestDispatcher("/EmployeeLogout.html");
								PrintWriter out = response.getWriter();
								out.println("<html><body>");
								out.println("<h1>Car registered successfully!</h1>");
								out.println("</body></html>");
								rd.include(request, response);

							} else {
								// If there is a car registered with the
								// same
								// license plate in database inform user
								RequestDispatcher rd = getServletContext().getRequestDispatcher("/Carform.html");
								PrintWriter out = response.getWriter();
								out.println("<font color=red>Car with license plate: " + carPlate
										+ " already exists in database.</font>");
								out.println("<font color=red>Please check again your license plate number.</font>");
								rd.include(request, response);
							}

						}
						// If updating database was unsuccessful inform user
						catch (SQLException e) {
							e.printStackTrace();
							RequestDispatcher rd = getServletContext().getRequestDispatcher("/Carform.html");
							PrintWriter out = response.getWriter();
							out.println(
									"<font color=red> Can not update database.Please check your connection.</font>");
							rd.include(request, response);
						}
					}
					// If inserted license plate was incorrect inform user
					else {
						RequestDispatcher rd = getServletContext().getRequestDispatcher("/Carform.html");
						PrintWriter out = response.getWriter();
						out.println(
								"<font color=red>License plate contains three capital letters and four numbers.</font>");
						out.println("<br>");
						out.println("<font color=red>Example : ABC-1234 </font>");
						rd.include(request, response);
					}

				}
				// If inserted release year was wrong inform user
				else {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/Carform.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red>Car release year should be greater than 1900 and less than "
							+ currentyear + ".</font>");
					rd.include(request, response);
				}

				// If input data were empty inform user that all fields are
				// obligatory
			} else {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/Carform.html");
				PrintWriter out = response.getWriter();
				out.println("<font color=red>All fields are obligatory.</font>");
				rd.include(request, response);
			}
		}
	}
}
