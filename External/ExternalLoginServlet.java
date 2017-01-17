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

		// user logged in
		if (request.getParameter("button1") != null) {

			// Get information user has provided
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			
			//DB INPUT
			String login = "valid";

			if (login.equals("valid")) {
				//In case of a valid login the user input is processed and he is redirected to the next page.
				
				// DB INPUT
				String exCustomerTRN = "";

				// Create new session
				HttpSession session = request.getSession();
				// Input data is stored in session for later use
				session.setAttribute("exCustomerTRN", exCustomerTRN);

				// DB INPUT
				String firstName = "";
				// A cookie is created containing the user's name.
				Cookie userName = new Cookie("userName", firstName);
				response.addCookie(userName);
				userName.setMaxAge(60 * 60 * 24);

				// DB OUTPUT

				response.sendRedirect("/ExternalCarForm.html");

			}else {
				//In case the email-password combo is invalid user is redirected to the login page with the according message.
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/ExternalLogin.html");
				PrintWriter out = response.getWriter();
				out.println("<font color=red>Either user name or password is wrong. Please try again.</font>");
				rd.include(request, response);
			}
			// user signed up
		} else if (request.getParameter("button2") != null) {
			
			//User input is processed and he is redirected to the next page.
			
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String phoneNumber = request.getParameter("phoneNumber");
			String TRN = request.getParameter("TRN");

			// Create new session
			HttpSession session = request.getSession();
			// Input data is stored in session for later use
			session.setAttribute("customerTRN", TRN);

			// A cookie is created containing the user's name.
			Cookie userName = new Cookie("userName", firstName);
			response.addCookie(userName);
			userName.setMaxAge(60 * 60 * 24);

			// DB OUTPUT

			response.sendRedirect("/ExternalCarForm.html");

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
