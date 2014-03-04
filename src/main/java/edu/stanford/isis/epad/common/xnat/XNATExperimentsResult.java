package edu.stanford.isis.epad.common.xnat;

import java.util.Collections;
import java.util.List;

/**
 * A result from XNAT listing a set of experiments.
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
 *    "totalRecords": "1"
 *  }
 * }
 * </code>
 * 
 * @author martin
 */
public class XNATExperimentsResult
{
	public final XNATExperimentResultSet ResultSet;

	public XNATExperimentsResult(List<XNATExperimentDescription> Result)
	{
		this.ResultSet = new XNATExperimentResultSet(Result);
	}

	public XNATExperimentsResult()
	{
		this.ResultSet = new XNATExperimentResultSet();
	}

	public static XNATExperimentsResult emptyExperiments()
	{
		return new XNATExperimentsResult();
	}

	public class XNATExperimentResultSet
	{
		public final List<XNATExperimentDescription> Result;
		public final int totalRecords;

		public XNATExperimentResultSet(List<XNATExperimentDescription> Result)
		{
			this.Result = Collections.unmodifiableList(Result);
			this.totalRecords = Result.size();
		}

		public XNATExperimentResultSet()
		{
			this.Result = Collections.emptyList();
			this.totalRecords = 0;
		}
	}
}