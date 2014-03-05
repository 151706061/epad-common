package edu.stanford.isis.epad.common.query;

import com.google.gson.Gson;

/**
 * A description of an XNAT experiment.
 * <p>
 * Example query:
 * <p>
 * <code>curl -b JSESSIONID=[session_key] -X GET "http:[host:port]/epad/projects/[project_id]/subjects/[subject_id]/experiments"</code>
 * <p>
 * Redirects to the XNAT call <code>/xnat/data/projects/..</code>, which returns a JSON-specified list of subjects for
 * the specified project, e.g.,
 * <p>
 * <code>
 * {
 * "ResultSet":
 *  { "Result":
 *    [ 
 *      {"project":"PRJ1","xsiType":"...","ID":"...","insert_date":"...","label":"SID1", "date":"...", "URI":"/data/subjects/EPAD_S00002"}  
 *    ], 
 *    "totalRecords" : "1"
 *  }
 * }
 * </code>
 * 
 * @author martin
 */
public class XNATExperiment
{
	public final String project, xsiType, id, insert_date, label, date, uri;

	public XNATExperiment(String project, String xsiType, String id, String insert_date, String label, String date,
			String uri)
	{
		this.project = project;
		this.xsiType = xsiType;
		this.id = id;
		this.insert_date = insert_date;
		this.label = label;
		this.date = date;
		this.uri = uri;
	}

	public String toJSON()
	{
		Gson gson = new Gson();

		return gson.toJson(this);
	}
}
