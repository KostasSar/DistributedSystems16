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

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet("/MechanicServlet")
public class MechanicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// allow access only if cookie exists
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

			Connection con = (Connection) getServletContext().getAttribute("DBConnection");
			PreparedStatement ps = null;
			ResultSet rs = null;

			// Depending on which "Submit" button was pressed in the
			// "AdminActions.html" the according code is executed.

			// Update vehicle's condition
			if (request.getParameter("button1") != null) {

				// Request data from form
				String lplate = request.getParameter("carplate");
				String condition = request.getParameter("carcondition");
				int releaseyear;
				String state = "Confirmed";

				// Check if inserted data is empty
				if (!lplate.isEmpty() && !condition.isEmpty()) {

					// Check if license plate is correct
					if (lplate.length() == 8 && lplate.matches("^[A-Z][A-Z][A-Z][-][0-9][0-9][0-9].$")) {

						// Check if license plate exists and store vehicle's
						// release year from database

						try {

							ps = con.prepareStatement("SELECT Release_Year FROM VEHICLE WHERE License_PLate=?");
							ps.setString(1, lplate);
							rs = ps.executeQuery();
							rs.next();
							releaseyear = rs.getInt("Release_Year");
					

								// Calculate cash prize
								float factor = 0;
								float amount = 0;
								float cash = 0;

								int currentyear = Calendar.getInstance().get(Calendar.YEAR);

								int year = (currentyear - releaseyear);
								if (condition.equals("Bad")) {
									factor = (float) 1 / 2;
								} else if (condition.equals("Moderate")) {
									factor = (float) 8 / 10;
								} else if (condition.equals("Good")) {
									factor = 1;
								}
								if (year < 5 && year > 0) {
									amount = 800;
								} else if (year >= 5 && year < 10) {
									amount = 1000;
								} else if (year >= 10 && year < 20) {
									amount = 1200;
								} else if (year > 20) {
									amount = 1000;
								}

								cash = (amount * factor);

								// Update database
								ps = con.prepareStatement(
										"UPDATE VEHICLE SET Vehicle_Condition=?,State=?,Cash_Prize=?,Examination_Date=? WHERE License_Plate=? ");
								ps.setString(1, condition);
								ps.setString(2, state);
								ps.setFloat(3, cash);
								ps.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now()));
								ps.setString(5, lplate);
								ps.executeUpdate();
								PrintWriter out = response.getWriter();

								RequestDispatcher rd = getServletContext()
										.getRequestDispatcher("/MechanicActions.html");
								out.println("<font color=black>Cash Reward for " + lplate + " is : " + cash
										+ " Euro </font>");
								rd.include(request, response);

						} catch (SQLException e) {
							e.printStackTrace();
							RequestDispatcher rd = getServletContext()
									.getRequestDispatcher("/MechanicActions.html");
							PrintWriter out = response.getWriter();
							out.println("<font color=red>License plate does not exist in database.</font>");
							rd.include(request, response);

						}

					} // If inserted license plate was incorrect inform user
					else {
						RequestDispatcher rd = getServletContext().getRequestDispatcher("/MechanicActions.html");
						PrintWriter out = response.getWriter();
						out.println(
								"<font color=red>License plate contains three capital letters and four numbers.</font>");
						out.println("<br>");
						out.println("<font color=red>Example : ABC-1234 </font>");
						rd.include(request, response);
					}
				} else {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/MechanicActions.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red>All fields are obligatory.</font>");
					rd.include(request, response);
				}

			}
			// View vehicle information button
			else if (request.getParameter("button2") != null) {

				String state = "Unchecked";
				int store = Integer.parseInt(department);

				try {
					// Select account from database
					ps = con.prepareStatement(
							"SELECT * FROM VEHICLE  WHERE State=? AND Store=? ORDER BY Entry limit 1 ");
					ps.setString(1, state);
					ps.setInt(2, store);
					rs = ps.executeQuery();

					if (!rs.isBeforeFirst()) {
						RequestDispatcher rd = getServletContext().getRequestDispatcher("/MechanicActions.html");
						PrintWriter out = response.getWriter();
						out.println("<font color=red>There are no cars for further check.</font>");
						rd.include(request, response);
					} else {
						// Print information to user
						while (rs.next()) {
							RequestDispatcher rd = getServletContext().getRequestDispatcher("/MechanicActions.html");
							PrintWriter out = response.getWriter();
							long trn = rs.getLong("Owner_Trn");
							String model = rs.getString("Model");
							String license = rs.getString("License_Plate");
							String ftype = rs.getString("Fuel_Type");
							String vcond = rs.getString("Vehicle_Condition");
							int year = rs.getInt("Release_Year");
							Date date = rs.getDate("Registration_Date");
							out.println("<h3>Vehicle's info:</h3>");
							out.println("<p>Owner TRN: " + trn + "</p>");
							out.println("<p>Vehicle model: " + model + "</p>");
							out.println("<p>License plate: " + license + "</p>");
							out.println("<p>Fuel type: " + ftype + "</p>");
							out.println("<p>Vehicle condition: " + vcond + "</p>");
							out.println("<p>Release year : " + year + "</p>");
							out.println("<p>Registration Date : " + date + "</p>");
							rd.include(request, response);

						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/MechanicActions.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red>Can not connect with database.</font>");
					rd.include(request, response);
				}

			} // Logout button
			else if (request.getParameter("button3") != null) {

				// If user logs out all cookies are deleted.
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						cookie.setMaxAge(0);
					}
				}

				// User is redirected to the log in page.
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login.html");

				// Inform user that the logout was successful.
				PrintWriter out = response.getWriter();
				out.println("<html><body>");
				out.println("<h1>Logout successful.</h1>");
				out.println("</body></html>");
				rd.include(request, response);

			} // Return to menu button
			else if (request.getParameter("button4") != null) {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/Menu.html");

				// Inform user that the logout was successful.
				PrintWriter out = response.getWriter();
				out.println("<html><body>");
				out.println("<h1>Welcome back " + userName + " !</h1>");
				out.println("</body></html>");
				rd.include(request, response);
			}
		}

	}
}
