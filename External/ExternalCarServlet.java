package servlets;

import java.io.IOException;

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

			String model = request.getParameter("model");
			String licensePlate = request.getParameter("licensePlate");
			String fuel = request.getParameter("fuel");
			String releaseYear = request.getParameter("releaseYear");
			String condition = request.getParameter("condition");
			String pickup = request.getParameter("pickup");

			String userTRN = null;
			HttpSession session = request.getSession(true);
			userTRN = (String) session.getAttribute(userTRN);
			
			if (pickup.equals("yes")) {
				// CALCULATE DISTANCE FROM MAP INPUT.

			}

			// DB OUTPUT

			RequestDispatcher rd = getServletContext().getRequestDispatcher("/ExternalConfirmation.html");
			rd.include(request, response);
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
