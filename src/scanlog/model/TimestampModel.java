package scanlog.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the list of startRendering and getRendering of each document.
 * @author Leandro Ferreira
 *
 */
public class TimestampModel {
	
	private List<String> startRenderingList = new ArrayList<>();
	
	private List<String> renderingList = new ArrayList<>();

	public List<String> getStartRenderingList() {
		return startRenderingList;
	}

	public void setStartRenderingList(List<String> startRenderingList) {
		this.startRenderingList = startRenderingList;
	}

	public List<String> getRenderingList() {
		return renderingList;
	}

	public void setRenderingList(List<String> renderingList) {
		this.renderingList = renderingList;
	}

}
