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
 * Servlet implementation class TEST
 */
@WebServlet("/Menu")
public class Menu extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Menu() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Allow access only if cookie exists
		Cookie[] cookies = request.getCookies();
		String userName = null;
		String userActivities = null;
		String rc = null;
		String rv = null;
		String wc = null;
		String wv = null;
		String cv = null;
		String dp = null;
	

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("userName")) {
					userName = cookie.getValue();
				}
				if (cookie.getName().equals("userRights")) {
					userActivities = cookie.getValue();
					String[] parts = userActivities.split("-");
					rc = parts[0];
					rv = parts[1];
					wc = parts[2];
					wv = parts[3];
					cv = parts[4];
					dp = parts[5];
					

				}

			}
		}

		if (userName == null) {
			response.sendRedirect("Menu.html");
		} else {
			if (request.getParameter("button1") != null) {
				if (wc.equals("wc") && wv.equals("wv") && rc.equals("rc")) {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/CustomerRegistration.html");
					PrintWriter out = response.getWriter();
					out.println("<html><body>");
					out.println("<h1>Welcome " + userName + "</h1>");
					out.println("</body></html>");
					rd.include(request, response);
				} else {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/Menu.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red>You are not allowed to register new customer and vehicle.</font>");
					rd.include(request, response);
				}
			}
			if (request.getParameter("button2") != null) {
				if (rv.equals("rv") && wv.equals("wv") && cv.equals("cv")) {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/MechanicActions.html");
					PrintWriter out = response.getWriter();
					out.println("<html><body>");
					out.println("<h1>Welcome " + userName + "</h1>");
					out.println("</body></html>");
					rd.include(request, response);
				} else {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/Menu.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red>You are not allowed to modify vehicle list.</font>");
					rd.include(request, response);
				}
			}
			if (request.getParameter("button3") != null) {
				if (dp.equals("dp")) {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/Cash.html");
					PrintWriter out = response.getWriter();
					out.println("<html><body>");
					out.println("<h1>Welcome " + userName + "</h1>");
					out.println("</body></html>");
					rd.include(request, response);
				} else {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/Menu.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red>You are not allowed to deliver customer's prize.</font>");
					rd.include(request, response);
				}
				if (request.getParameter("button4") != null) {

					RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login.html");
					PrintWriter out = response.getWriter();
					out.println("<html><body>");
					out.println("<h1>Logout was successful.</h1>");
					out.println("</body></html>");
					rd.include(request, response);
				}
			}

		}

	}
}
