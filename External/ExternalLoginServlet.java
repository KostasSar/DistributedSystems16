package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
 * Servlet implementation class ExternalLoginServlet
 */
@WebServlet("/ExternalLoginServlet")
public class ExternalLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Create connection
		Connection con = (Connection) getServletContext().getAttribute("DBConnection");
		PreparedStatement ps = null;
		ResultSet rs = null;

		// user logged in
		if (request.getParameter("button1") != null) {

			// Get information user has provided
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			System.out.println(" a "+email+" pas "+password);
			
			// Check if user account exists
			try {
				ps = con.prepareStatement("Select * from WebUsers WHERE Email=? AND Password=?");
				ps.setString(1, email);
				ps.setString(2, password);
				rs = ps.executeQuery();

				// If user information is incorrect
				if (!rs.isBeforeFirst()) {
					// In case the email-password combo is invalid user is
					// redirected to the login page with the according message.
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/ExternalLogin.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red>Either user name or password is wrong. Please try again.</font>");
					rd.include(request, response);
				}
				// In case of valid login, store user's information from
				// database
				//else {

					rs.next();
					Long exCustomerTRN = rs.getLong("TRN");
					String firstName = rs.getString("Name");
					// The user input is processed and he is redirected to the
					// next page.

					// Create new session
					HttpSession session = request.getSession();
					// Input data is stored in session for later use
					session.setAttribute("exCustomerTRN", exCustomerTRN);

					// A cookie is created containing the user's name.
					Cookie userName = new Cookie("userName", firstName);
					response.addCookie(userName);
					userName.setMaxAge(60 * 60 * 24);

					response.sendRedirect("/ExternalCarForm.html");
				//}

			} // In case connection with database fails inform users
			catch (SQLException e) {
				e.printStackTrace();
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/ExternalLogin.html");
				PrintWriter out = response.getWriter();
				out.println("<font color=red>Connection with database failed.Please try again.</font>");
				rd.include(request, response);
			}

			// User signed up
		} else if (request.getParameter("button2") != null) {

			// User input is processed and he is redirected to the next page.

			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String pnumber = request.getParameter("phoneNumber");
			String TRN = request.getParameter("TRN");

			// Check if TRN length is correct so it can be stored
			// successfully in database
			if (TRN.length() == 9) {

				// Convert variables to long so they can be stored in database
				try {

					Long ctrn = Long.parseLong(TRN);
					Long phonenumber = Long.parseLong(pnumber);

					try {

						ps = con.prepareStatement("INSERT INTO WebUsers VALUES (?,?,?,?,?,?)");
						ps.setString(1, firstName);
						ps.setString(2, lastName);
						ps.setLong(3, ctrn);
						ps.setLong(4, phonenumber);
						ps.setString(5, email);
						ps.setString(6, password);
						ps.executeUpdate();

						// Create new session
						HttpSession session = request.getSession();
						// Input data is stored in session for later use
						session.setAttribute("customerTRN", TRN);

						// A cookie is created containing the user's name.
						Cookie userName = new Cookie("userName", firstName);
						response.addCookie(userName);
						userName.setMaxAge(60 * 60 * 24);
						response.sendRedirect("/ExternalCarForm.html");

					} catch (SQLException e) {

						e.printStackTrace();
						RequestDispatcher rd = getServletContext().getRequestDispatcher("/ExternalLogin.html");
						PrintWriter out = response.getWriter();
						out.println("<font color=red>Connection with database failed.Please try again.</font>");
						rd.include(request, response);
					}
				} catch (NumberFormatException e) {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/ExternalLogin.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red>Tax Registration Number or Phone number contains letters.</font>");
					rd.include(request, response);
				}
			} else {
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/ExternalLogin.html");
				PrintWriter out = response.getWriter();
				out.println("<font color=red>Tax Registration Number should be 9 digits long, not " + TRN.length()
						+ " .</font>");
				rd.include(request, response);
			}
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Handles the request from the Welcome page. Redirects user to the
		// login-signup page.
		if (request.getParameter("button3") != null) {
			response.sendRedirect("/Welcome.html");
		}
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ExternalLoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

}
