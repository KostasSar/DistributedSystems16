
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
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String userID = "admin";
	private final String password = "password";

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String user = request.getParameter("uname");
		String pwd = request.getParameter("upsw");

		// Create a new session
		HttpSession session = request.getSession();

		// After 30 minutes of inactivity the session ends.
		session.setMaxInactiveInterval(60 * 30);

		if (!user.isEmpty() && !pwd.isEmpty()) {
			// Check if user is administrator
			if (user.equals(userID) && pwd.equals(password)) {

				// A cookie is created containing the username
				Cookie userName = new Cookie("userName", user);
				userName.setMaxAge(30 * 60);
				response.addCookie(userName);
				// Setting cookie to expire in 30 minutes
				userName.setMaxAge(60 * 60 * 24);

				// Redirect admin to his web page
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
				PrintWriter out = response.getWriter();
				out.println("<html><body>");
				out.println("<h1>Welcome " + user + "</h1>");
				out.println("</body></html>");
				rd.include(request, response);

			} // If the user is not the admin, check whether he/she is an
				// employee
				// or a mechanic
			else {

				// Create new connection object
				Connection con = (Connection) getServletContext().getAttribute("DBConnection");
				PreparedStatement ps = null;
				ResultSet rs = null;
				String userActivities;

				try {

					// Check database if user has provided correct login
					// information
					ps = con.prepareStatement("SELECT Password,Id,Actions FROM USERS WHERE Password=? and Id=?");
					ps.setString(1, pwd);
					ps.setString(2, user);
					rs = ps.executeQuery();
					rs.next();
					userActivities = rs.getString("Actions");

					// If id and password match

					if (user.charAt(1) == 'm') {
						// A cookie is created containing the user's status
						Cookie userType = new Cookie("userType", "Mechanic");
						response.addCookie(userType);
						userType.setMaxAge(60 * 60 * 24);

					} else if (user.charAt(1) == 'e') {
						// A cookie is created containing the user's status
						Cookie userType = new Cookie("userType", "Employee");
						response.addCookie(userType);
						userType.setMaxAge(60 * 60 * 24);
					}

					// A cookie is created containing user's department
					String userDept = Character.toString(user.charAt(0));
					Cookie userDepartment = new Cookie("userDepartment", userDept);
					response.addCookie(userDepartment);
					// Setting cookie to expire in 30 minutes
					userDepartment.setMaxAge(60 * 60 * 24);

					// A cookie is created containing the username
					Cookie userName = new Cookie("userName", user);
					response.addCookie(userName);
					// Setting cookie to expire in 30 minutes
					userName.setMaxAge(60 * 60 * 24);

					// A cookie is created containing user's rights
					Cookie userRights = new Cookie("userRights", userActivities);
					response.addCookie(userRights);
					// Setting cookie to expire in 30 minutes
					userRights.setMaxAge(60 * 60 * 24);

					RequestDispatcher rd = getServletContext().getRequestDispatcher("/Menu.html");
					PrintWriter out = response.getWriter();
					out.println("<html><body>");
					out.println("<h1>Welcome " + user + "</h1>");
					out.println("</body></html>");
					rd.include(request, response);

				}

				catch (SQLException e) {
					e.printStackTrace();
					// In case of an invalid login user is redirected to the
					// login
					// page
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red>Either user name or password is wrong. Please try again.</font>");
					rd.include(request, response);
				}

			}

		} else {
			// In case of empty entry redirect user to the login page
			RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login.html");
			PrintWriter out = response.getWriter();
			out.println("<font color=red>User id and password  can not be empty.Please try again.</font>");
			rd.include(request, response);
		}
	}
}
