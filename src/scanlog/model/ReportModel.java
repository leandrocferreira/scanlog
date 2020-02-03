package scanlog.model;

/**
 * Represents the identification of each document.
 * @author Leandro Ferreira
 *
 */
public class ReportModel {
	
	private String documentId;
	
	private String page;
	
	private String startRenderingUID;
	
	private String startRenderingTimestamp; 

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getStartRenderingUID() {
		return startRenderingUID;
	}

	public void setStartRenderingUID(String startRenderingUID) {
		this.startRenderingUID = startRenderingUID;
	}

	public String getStartRenderingTimestamp() {
		return startRenderingTimestamp;
	}

	public void setStartRenderingTimestamp(String startRenderingTimestamp) {
		this.startRenderingTimestamp = startRenderingTimestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((startRenderingUID == null) ? 0 : startRenderingUID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportModel other = (ReportModel) obj;
		if (startRenderingUID == null) {
			if (other.startRenderingUID != null)
				return false;
		} else if (!startRenderingUID.equals(other.startRenderingUID))
			return false;
		return true;
	}
}
