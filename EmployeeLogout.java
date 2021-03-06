

package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CustomerRegisterServlet
 */
@WebServlet("/EmployeeLogout")
public class EmployeeLogout extends HttpServlet {
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

			if (request.getParameter("button1") != null) {
				// Redirect user to customer registration page
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/Menu.html");
				rd.include(request, response);
			} else if (request.getParameter("button2") != null) {
				// If user logs out all cookies are deleted.
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						cookie.setMaxAge(0);
					}
				}

				// Redirect user to the logout page
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login.html");
				PrintWriter out = response.getWriter();
				out.println("<html><body>");
				out.println("<h1>Logout was successful.</h1>");
				out.println("</body></html>");
				rd.include(request, response);
			} else if (request.getParameter("button3") != null) {
				// Redirect user to vehicle registration page
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/Carform.html");
				rd.include(request, response);
			}
		}
	}
}
