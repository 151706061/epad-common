package edu.stanford.isis.epad.common.xnat;

/**
 * A description of an XNAT project.
 * <p>
 * <code>/xnat/data/projects</code> returns a JSON-specified list of projects for the current user, e.g.,
 * <p>
 * <code>
 * {
 * "ResultSet":
 *  { "Result":
 *    [ 
 *     {"secondary_ID":"epad-xnat","pi_lastname":"MrPI","description":"", "name":"XNAT Project","ID":"EPAD_PROJECT","pi_firstname":"An","URI":"/data/projects/EPAD_PROJECT"},
 *    ], 
 *    "totalRecords": "1"
 *  }
 * }
 * </code>
 * 
 * @author martin
 */
public class XNATProjectDescription
{
	public final String secondaryID, piLastName, description, name, id, piFirstName, uri;

	public XNATProjectDescription(String secondaryID, String piLastName, String description, String name, String id,
			String piFirstName, String uri)
	{
		this.secondaryID = secondaryID;
		this.piLastName = piLastName;
		this.description = description;
		this.name = name;
		this.id = id;
		this.piFirstName = piFirstName;
		this.uri = uri;
	}
}
