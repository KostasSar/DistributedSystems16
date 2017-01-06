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

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
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
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("userName")) {
					userName = cookie.getValue();
				}
			}
		}

		if (userName == null) {
			response.sendRedirect("Login.html");
		}

		else {
			Connection con = (Connection) getServletContext().getAttribute("DBConnection");
			PreparedStatement ps = null;
			ResultSet rs = null;

			// Depending on which "Submit" button was pressed in the
			// "AdminActions.html" the according code is executed.

			// Create new user account button
			if (request.getParameter("button1") != null) {

				// Get information user has provided
				String newName = request.getParameter("uname");
				String newSurname = request.getParameter("usurname");
				String newId = request.getParameter("uid");
				String newPassword = request.getParameter("upwd");
				String rc = request.getParameter("rc");
				String rv = request.getParameter("rv");
				String wc = request.getParameter("wc");
				String wv = request.getParameter("wv");
				String cv = request.getParameter("cv");
				String dp = request.getParameter("dp");
				String recv = request.getParameter("recv");
				String ma = request.getParameter("ma");

				// Check if provided information is empty
				if (!newName.isEmpty() && !newSurname.isEmpty() && !newId.isEmpty() && !newPassword.isEmpty()) {
						String newPosition = request.getParameter("position");
						String userActivities = rc+"-"+rv+"-"+wc+"-"+wv+"-"+cv+"-"+dp+"-"+recv+"-"+ma;
						String store = request.getParameter("store");
						String storecheck = newId.substring(0, 1);
						int counter = 0;
						int check = 0;
						
						
						
						String[] parts = userActivities.split("-");
						String act1 = parts[0];
						String act2 = parts[1];
						String act3 = parts[2];
						String act4 = parts[3];
						String act5 = parts[4];
						String act6 = parts[5];
						String act7 = parts[6];
						String act8 = parts[7];
						
						System.out.println("1:" + act1 + " 2: " +act2+ " 3: " + act3 + " 4: " +act4+ " 5: " +act5+ " 6: " +act6+ " 7: " +act7 + " 8: " +act8);
						
						
						
						// Check if provided number matches with user id
						if (storecheck.equals(store)) {

							int newStore = Integer.parseInt(store);

							// Check if user id pattern is correct
							if (newId.matches("^[1-5][me][0-9][0-9]$")) {

								// Check if second user_id letter matches with
								// user's
								// position
								if (newPosition.equals("Employee") && newId.charAt(1) == 'e') {

									// If administrator wants to create a new
									// employee
									// account
									// Check database for the total number of
									// employees.Each
									// store should have three employees

									try {
										ps = con.prepareStatement(
												"SELECT COUNT(*) AS total FROM USERS WHERE Position=? AND Department=?");
										ps.setString(1, newPosition);
										ps.setInt(2, newStore);
										rs = ps.executeQuery();

										while (rs.next()) {
											counter = rs.getInt("total");
										}

										// If there are 3 or more employees in
										// selected
										// store, then inform admin that they
										// can't
										// create a
										// new user account
										if (counter >= 3) {
											RequestDispatcher rd = getServletContext()
													.getRequestDispatcher("/AdminActions.html");
											PrintWriter out = response.getWriter();
											out.println(
													"<font color=red>There is no vacancy for this store.There are already "
															+ counter + " employees.</font>");
											rd.include(request, response);
											check = 1;
										}
									} catch (SQLException e) {
										e.printStackTrace();
									}

									// If administrator wants to create a new
									// mechanic
									// account
								} else if (newPosition.equals("Mechanic") && newId.charAt(1) == 'm') {

								
									// Check database if there is already a
									// mechanic
									// in
									// selected store.Each store should have
									// only
									// one
									// mechanic
									try {
										ps = con.prepareStatement(
												"SELECT COUNT(*) AS total FROM USERS WHERE Position=? AND Department=?");
										ps.setString(1, newPosition);
										ps.setInt(2, newStore);
										rs = ps.executeQuery();
										while (rs.next()) {
											counter = rs.getInt("total");
										}
										System.out.println("" + counter);
										// If there is already a mechanic in
										// selected store,
										// then inform admin that they can't
										// create
										// a new
										// user account
										if (counter >= 1) {
											RequestDispatcher rd = getServletContext()
													.getRequestDispatcher("/AdminActions.html");
											PrintWriter out = response.getWriter();
											out.println(
													"<font color=red>There is no vacancy for this store.There is already "
															+ counter + " mechanic.</font>");
											rd.include(request, response);
											check = 1;
										}
									} catch (SQLException e) {
										e.printStackTrace();
									}
								} else {
									RequestDispatcher rd = getServletContext()
											.getRequestDispatcher("/AdminActions.html");
									PrintWriter out = response.getWriter();
									out.println(
											"<font color=red>Second letter in user id does not match with provided user position.</font>");
									rd.include(request, response);
									check = 1;
								}
								if (check == 0) {
									try {

										// Check database if there is a user
										// with
										// the same id.User id must be empty.
										ps = con.prepareStatement("SELECT * FROM USERS WHERE Id=?");
										ps.setString(1, newId);
										rs = ps.executeQuery();

										// If provided id already exists in
										// database
										// inform admin
										if (rs != null && rs.next()) {
											RequestDispatcher rd = getServletContext()
													.getRequestDispatcher("/AdminActions.html");
											PrintWriter out = response.getWriter();
											out.println("<font color=red>User id " + newId
													+ " is taken.Please insert a new one.</font>");
											rd.include(request, response);
										} else {

											// Update database
											ps = con.prepareStatement("INSERT INTO USERS VALUES (?,?,?,?,?,?,?)");
											ps.setString(1, newName);
											ps.setString(2, newSurname);
											ps.setString(3, newPassword);
											ps.setString(4, newId);
											ps.setInt(5, newStore);
											ps.setString(6, newPosition);
											ps.setString(7, userActivities);
											ps.executeUpdate();

											// If update was successful inform
											// Administrator

											RequestDispatcher rd = getServletContext()
													.getRequestDispatcher("/AdminActions.html");
											PrintWriter out = response.getWriter();
											out.println("<html><body>");
											out.println("<h1>New user account successfully created.</h1>");
											out.println("</body></html>");
											rd.include(request, response);

										}
									}

									catch (SQLException e) {
										e.printStackTrace();
										RequestDispatcher rd = getServletContext()
												.getRequestDispatcher("/AdminActions.html");
										PrintWriter out = response.getWriter();
										out.println("<html><body>");
										out.println(
												"<h1>Failed to create new user account.Please check your connection.</h1>");
										out.println("</body></html>");
										rd.include(request, response);
									}
								}

							} else {
								RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
								PrintWriter out = response.getWriter();
								out.println("<font color=red> User id is not correct.</font>");
								out.println(
										"<font color=red> User id pattern is:  store number - position [ m  or e] - two digit user code.</font>");
								rd.include(request, response);
							}
						} else {
							RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
							PrintWriter out = response.getWriter();
							out.println(
									"<font color=red> The store number you have provided does not match with first user id letter.</font>");
							rd.include(request, response);
						}
					
				} else {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red> All fields are obligatory.</font>");
					rd.include(request, response);
				}
			}

			// Modify user account button
			else if (request.getParameter("button2") != null) {

				String modId = request.getParameter("idfinderr");
				String newName = request.getParameter("uname");
				String newSurname = request.getParameter("usurname");
				String newPassword = request.getParameter("upsw");
				String nid = request.getParameter("uid");
				String savedName = null;
				String savedSurname = null;
				String savedPassword = null;
				String savedId = null;
				int savedstore = 0;
				String savedPosition = null;
				//int check = 0;
				String userActivities = null;
				String rc = request.getParameter("rc");
				String rv = request.getParameter("rv");
				String wc = request.getParameter("wc");
				String wv = request.getParameter("wv");
				String cv = request.getParameter("cv");
				String dp = request.getParameter("dp");
				String recv = request.getParameter("recv");
				String ma = request.getParameter("ma");


				// Check if user id field is empty
				if (!modId.isEmpty()) {

					try {

						// Store data from database and check if provided id
						// exists
						ps = con.prepareStatement("SELECT * FROM USERS WHERE Id = ?");
						ps.setString(1, modId);
						rs = ps.executeQuery();
						userActivities = rc+"-"+rv+"-"+wc+"-"+wv+"-"+cv+"-"+dp+"-"+recv+"-"+ma;
						if (!rs.isBeforeFirst()) {
							RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
							PrintWriter out = response.getWriter();
							out.println("<font color=red>User id could not be found in database.</font>");
							rd.include(request, response);
						} else {
							while (rs.next()) {
								savedName = rs.getString("Name");
								savedSurname = rs.getString("Surname");
								savedPassword = rs.getString("Password");
								savedId = rs.getString("Id");
								savedstore = rs.getInt("Department");
								savedPosition = rs.getString("Position");
							}

							// Check if name field is empty
							if (!newName.equals("null") && !newName.isEmpty()) {
								savedName = newName;
							}
							// Check if surname field is empty
							if (!newSurname.equals("null") && !newSurname.isEmpty()) {
								savedSurname = newSurname;
							}
							// Check if password field is empty
							if (!newPassword.equals("null") && !newPassword.isEmpty()) {
								savedPassword = newPassword;
							}
							// Check user id field. If it is not empty, request
							// data
							// from form
							if (!nid.equals("null") && !nid.isEmpty()) {
								
								// Check if new user id is correct
								if (nid.matches("^[1-5][me][0-9][0-9]$")) {
									savedId = nid;
									// Store department and user's position
									savedstore = Character.getNumericValue(nid.charAt(0));

									if (nid.charAt(1) == 'e') {
										savedPosition = "Employee";
										
									} else if (nid.charAt(1) == 'm') {
										savedPosition = "Mechanic";
									}

								} else {

									RequestDispatcher rd = getServletContext()
											.getRequestDispatcher("/AdminActions.html");
									PrintWriter out = response.getWriter();
									out.println("<font color=red> User id is not correct.</font>");
									out.println(
											"<font color=red> User id pattern is:  store number - position [ m  or e] - two digit user code.</font>");
									rd.include(request, response);
									
								}
							}

								try {
									// Update database
									ps = con.prepareStatement(
											"UPDATE USERS SET Name=?,Surname=?,Id =?,Password=?, Department=?, Position=?,Actions=?  WHERE Id=? ");
									ps.setString(1, savedName);
									ps.setString(2, savedSurname);
									ps.setString(3, savedId);
									ps.setString(4, savedPassword);
									ps.setInt(5, savedstore);
									ps.setString(6, savedPosition);
									ps.setString(7, userActivities);
									ps.setString(8, modId);
									ps.executeUpdate();

									RequestDispatcher rd = getServletContext()
											.getRequestDispatcher("/AdminActions.html");
									PrintWriter out = response.getWriter();
									out.println("<html><body>");
									out.println("<h1>User account has been successfully modified.</h1>");
									out.println("</body></html>");
									rd.include(request, response);

								} catch (SQLException e) {
									e.printStackTrace();
									RequestDispatcher rd = getServletContext()
											.getRequestDispatcher("/AdminActions.html");
									PrintWriter out = response.getWriter();
									out.println("<html><body>");
									out.println("<h1>Failed to modify  user account.</h1>");
									out.println("</body></html>");
									rd.include(request, response);
								}

							
						}
					} catch (SQLException e) {
						e.printStackTrace();
						RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
						PrintWriter out = response.getWriter();
						out.println(
								"<font color=red>Can not connect with database,please check your connection.</font>");
						rd.include(request, response);
					}
				} else {
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
					PrintWriter out = response.getWriter();
					out.println("<font color=red>You need to choose an id.</font>");
					rd.include(request, response);
				}

			}
			// Delete user account button
			else if (request.getParameter("button3") != null) {

				String delId = request.getParameter("iddeleter");

				try {
					ps = con.prepareStatement("DELETE FROM USERS  WHERE Id=? ");
					ps.setString(1, delId);
					ps.executeUpdate();
					ps.executeUpdate();

					RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
					PrintWriter out = response.getWriter();
					out.println("<html><body>");
					out.println("<h1>User account successfully deleted.</h1>");
					out.println("</body></html>");
					rd.include(request, response);

				} catch (SQLException e) {
					e.printStackTrace();
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
					PrintWriter out = response.getWriter();
					out.println("<html><body>");
					out.println("<h1>Failed to delete user account.</h1>");
					out.println("</body></html>");
					rd.include(request, response);

				}
			}
			// View users button
			else if (request.getParameter("button4") != null) {

				try {
					// Load accounts from database
					ps = con.prepareStatement("SELECT * FROM USERS");
					rs = ps.executeQuery();
					PrintWriter out = response.getWriter();
					out.println("<h3>User's accounts:</h3>");
					while (rs.next()) {
						out.println("<h3>User:</h3>");
						String Name = rs.getString("Name");
						String Surname = rs.getString("Surname");
						String Id = rs.getString("Id");
						String Password = rs.getString("Password");
						String Position = rs.getString("Position");
						String Actions = rs.getString("Actions");
						int store = rs.getInt("Department");
						out.println("<p>Name : " + Name + "</p>");
						out.println("<p>Surname : " + Surname + "</p>");
						out.println("<p>Id : " + Id + "</p>");
						out.println("<p>Password : " + Password + "</p>");
						out.println("<p>Position : " + Position + "</p>");
						out.println("<p>Store id : " + store + "</p>");
						out.println("<p>User's Actions : " + Actions + "</p>");

					}
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
					rd.include(request, response);
				} catch (SQLException e) {
					e.printStackTrace();
					RequestDispatcher rd = getServletContext().getRequestDispatcher("/AdminActions.html");
					PrintWriter out = response.getWriter();
					out.println("<html><body>");
					out.println("<h1>Failed to load accounts from database.</h1>");
					out.println("</body></html>");
					rd.include(request, response);
				}

			}
			// Logout Button
			else if (request.getParameter("button5") != null) {

				// User is redirected to the log in page.
				RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login.html");

				// If the user logs out all cookies are deleted.
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						cookie.setMaxAge(0);
					}
				}
				// Inform user that the logout was successful.
				PrintWriter out = response.getWriter();
				out.println("<html><body>");
				out.println("<h1>Logout successful.</h1>");
				out.println("</body></html>");
				rd.include(request, response);

			}

		}
	}
}
