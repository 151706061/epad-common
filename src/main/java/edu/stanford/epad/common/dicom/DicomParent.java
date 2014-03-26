package edu.stanford.epad.common.dicom;

/**
 * Holds a IDCOM UID and the type.
 * 
 * @author amsnyder
 */
public class DicomParent
{
	final String dicomUID;
	final DicomParentType type;

	public DicomParent(String dicomUID, DicomParentType type)
	{
		this.dicomUID = dicomUID;
		this.type = type;
	}

	public String getDicomUID()
	{
		return dicomUID;
	}
}