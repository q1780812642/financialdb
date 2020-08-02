package org.jeecg.modules.demo.workflow.service.impl;

import org.flowable.bpmn.model.AssociationDirection;
import org.flowable.bpmn.model.GraphicInfo;
import org.flowable.image.exception.FlowableImageException;
import org.flowable.image.impl.DefaultProcessDiagramCanvas;
import org.flowable.image.util.ReflectUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class CustomProcessDiagramCanvas extends DefaultProcessDiagramCanvas {

	protected static Color LABEL_COLOR = new Color(0, 0, 0);

	//font
	protected String activityFontName = "宋体";
	protected String labelFontName = "宋体";
	protected String annotationFontName = "宋体";

	private static volatile boolean flag = false;

	public static final Color COLOR_NORMAL = new Color(0, 205, 0);
	public static final Color COLOR_CURRENT = new Color(255, 0, 0);
    /** 定义生成流程图时的边距(像素) **/
    public static final int PROCESS_PADDING = 5;

	public CustomProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType) {
		super(width, height, minX, minY, imageType);
	}

	public CustomProcessDiagramCanvas(int width, int height, int minX, int minY, String imageType,
                                      String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {
		super(width, height, minX, minY, imageType, activityFontName, labelFontName, annotationFontName,
				customClassLoader);
	}

	public void drawHighLight(boolean isStartOrEnd, int x, int y, int width, int height, Color color) {
		Paint originalPaint = g.getPaint();
		Stroke originalStroke = g.getStroke();

		g.setPaint(color);
		g.setStroke(MULTI_INSTANCE_STROKE);
		if (isStartOrEnd) {// 开始、结束节点画圆
			g.drawOval(x, y, width, height);
		} else {// 非开始、结束节点画圆角矩形
			RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 5, 5);
			g.draw(rect);
		}
		g.setPaint(originalPaint);
		g.setStroke(originalStroke);
	}

	public void drawSequenceflow(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault,
			boolean highLighted, double scaleFactor, Color color) {
		drawConnection(xPoints, yPoints, conditional, isDefault, "sequenceFlow", AssociationDirection.ONE, highLighted,
				scaleFactor, color);
	}

	public void drawConnection(int[] xPoints, int[] yPoints, boolean conditional, boolean isDefault,
			String connectionType, AssociationDirection associationDirection, boolean highLighted, double scaleFactor,
			Color color) {

		Paint originalPaint = g.getPaint();
		Stroke originalStroke = g.getStroke();

		g.setPaint(CONNECTION_COLOR);
		if (connectionType.equals("association")) {
			g.setStroke(ASSOCIATION_STROKE);
		} else if (highLighted) {
			g.setPaint(color);
			g.setStroke(HIGHLIGHT_FLOW_STROKE);
		}

		for (int i = 1; i < xPoints.length; i++) {
			Integer sourceX = xPoints[i - 1];
			Integer sourceY = yPoints[i - 1];
			Integer targetX = xPoints[i];
			Integer targetY = yPoints[i];
			Line2D.Double line = new Line2D.Double(sourceX, sourceY, targetX, targetY);
			g.draw(line);
		}

		if (isDefault) {
			Line2D.Double line = new Line2D.Double(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
			drawDefaultSequenceFlowIndicator(line, scaleFactor);
		}

		if (conditional) {
			Line2D.Double line = new Line2D.Double(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
			drawConditionalSequenceFlowIndicator(line, scaleFactor);
		}

		if (associationDirection.equals(AssociationDirection.ONE)
				|| associationDirection.equals(AssociationDirection.BOTH)) {
			Line2D.Double line = new Line2D.Double(xPoints[xPoints.length - 2], yPoints[xPoints.length - 2],
					xPoints[xPoints.length - 1], yPoints[xPoints.length - 1]);
			drawArrowHead(line, scaleFactor);
		}
		if (associationDirection.equals(AssociationDirection.BOTH)) {
			Line2D.Double line = new Line2D.Double(xPoints[1], yPoints[1], xPoints[0], yPoints[0]);
			drawArrowHead(line, scaleFactor);
		}
		g.setPaint(originalPaint);
		g.setStroke(originalStroke);
	}

	public void drawLabel(boolean highLighted, String text, GraphicInfo graphicInfo, boolean centered) {
		float interline = 1.0f;

		// text
		if (text != null && text.length() > 0) {
			Paint originalPaint = g.getPaint();
			Font originalFont = g.getFont();
			if (highLighted) {
				g.setPaint(COLOR_NORMAL);
			} else {
				g.setPaint(LABEL_COLOR);
			}
			g.setFont(new Font(labelFontName, Font.PLAIN, 12));

			int wrapWidth = 100;
			int textY = (int) graphicInfo.getY();

			// TODO: use drawMultilineText()
			AttributedString as = new AttributedString(text);
			as.addAttribute(TextAttribute.FOREGROUND, g.getPaint());
			as.addAttribute(TextAttribute.FONT, g.getFont());
			AttributedCharacterIterator aci = as.getIterator();
			FontRenderContext frc = new FontRenderContext(null, true, false);
			LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);

			while (lbm.getPosition() < text.length()) {
				TextLayout tl = lbm.nextLayout(wrapWidth);
				textY += tl.getAscent();
				Rectangle2D bb = tl.getBounds();
				double tX = graphicInfo.getX();
				if (centered) {
					tX += (int) (graphicInfo.getWidth() / 2 - bb.getWidth() / 2);
				}
				tl.draw(g, (float) tX, textY);

				textY += tl.getDescent() + tl.getLeading() + (interline - 1.0f) * tl.getAscent();

			}

			// restore originals
			g.setFont(originalFont);
			g.setPaint(originalPaint);

		}
	}

	public void drawTask(String name, GraphicInfo graphicInfo, boolean thickBorder, double scaleFactor) {
	        Paint originalPaint = g.getPaint();
	        int x = (int) graphicInfo.getX();
	        int y = (int) graphicInfo.getY();
	        int width = (int) graphicInfo.getWidth();
	        int height = (int) graphicInfo.getHeight();

	        // Create a new gradient paint for every task box, gradient depends on x and y and is not relative
	        //背景色
	        g.setPaint(TASK_BOX_COLOR);
	        //g.setPaint(new Color(135,206,235));

	        int arcR = 6;
	        if (thickBorder)
	            arcR = 3;

	        // shape
	        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, arcR, arcR);
	        g.fill(rect);
	        g.setPaint(TASK_BORDER_COLOR);//边框颜色

	        if (thickBorder) {
	            Stroke originalStroke = g.getStroke();
	            g.setStroke(THICK_TASK_BORDER_STROKE);
	            g.draw(rect);
	            g.setStroke(originalStroke);
	        } else {
	            g.draw(rect);
	        }

	        g.setPaint(originalPaint);
	    	g.setFont(new Font(labelFontName, Font.PLAIN, 12));
	        // text
	        if (scaleFactor == 1.0 && name != null && name.length() > 0) {
	            int boxWidth = width - (2 * TEXT_PADDING);
	            int boxHeight = height - 16 - ICON_PADDING - ICON_PADDING - MARKER_WIDTH - 2 - 2;
	            int boxX = x + width / 2 - boxWidth / 2;
	            int boxY = y + height / 2 - boxHeight / 2 + ICON_PADDING + ICON_PADDING - 2 - 2;

	            drawMultilineCentredText(name, boxX, boxY, boxWidth, boxHeight);
	        }
	    }

	@Override
	public BufferedImage generateBufferedImage(String imageType) {
		if (closed) {
			throw new FlowableImageException("ProcessDiagramGenerator already closed");
		}

		// Try to remove white space
		minX = (minX <= PROCESS_PADDING) ? PROCESS_PADDING : minX;
		minY = (minY <= PROCESS_PADDING) ? PROCESS_PADDING : minY;
		BufferedImage imageToSerialize = processDiagram;
		if (minX >= 0 && minY >= 0) {
			imageToSerialize = processDiagram.getSubimage(minX - PROCESS_PADDING,
					minY - PROCESS_PADDING, canvasWidth - minX + PROCESS_PADDING,
					canvasHeight - minY + PROCESS_PADDING);
		}
		return imageToSerialize;
	}

	@Override
	public void initialize(String imageType) {
		//BufferedImage.TYPE_INT_ARGB 背景色
		//this.processDiagram = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
		//this.g = processDiagram.createGraphics();

		if ("png".equalsIgnoreCase(imageType)) {
            this.processDiagram = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        } else {
            this.processDiagram = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        }

        this.g = processDiagram.createGraphics();
        if (!"png".equalsIgnoreCase(imageType)) {
            this.g.setBackground(new Color(255, 255, 255, 0));
            this.g.clearRect(0, 0, canvasWidth, canvasHeight);
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setPaint(Color.black);

		// 抗锯齿和高质量渲染
		RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);//呈现提示键。
		renderHints.put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);// 文本抗锯齿提示键。
		g.setRenderingHints(renderHints);
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setPaint(Color.black);

		//通用字体优化【节点字体】
		//Font font = new Font(activityFontName, Font.BOLD, FONT_SIZE);
		Font font = new Font(activityFontName, Font.PLAIN, FONT_SIZE);
		g.setFont(font);
		this.fontMetrics = g.getFontMetrics();

		LABEL_FONT = new Font(labelFontName, Font.ITALIC, 12);
		ANNOTATION_FONT = new Font(annotationFontName, Font.PLAIN, FONT_SIZE);
		// 优化加载速度
		if (flag) {
			return;
		}
		try {
			USERTASK_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/userTask.png", customClassLoader));
			SCRIPTTASK_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/scriptTask.png", customClassLoader));
			SERVICETASK_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/serviceTask.png", customClassLoader));
			RECEIVETASK_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/receiveTask.png", customClassLoader));
			SENDTASK_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/sendTask.png", customClassLoader));
			MANUALTASK_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/manualTask.png", customClassLoader));
			BUSINESS_RULE_TASK_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/businessRuleTask.png", customClassLoader));
			SHELL_TASK_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/shellTask.png", customClassLoader));
			CAMEL_TASK_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/camelTask.png", customClassLoader));
			MULE_TASK_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/muleTask.png", customClassLoader));

			TIMER_IMAGE = ImageIO.read(ReflectUtil.getResource("org/flowable/icons/timer.png", customClassLoader));
			COMPENSATE_THROW_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/compensate-throw.png", customClassLoader));
			COMPENSATE_CATCH_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/compensate.png", customClassLoader));
			ERROR_THROW_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/error-throw.png", customClassLoader));
			ERROR_CATCH_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/error.png", customClassLoader));
			MESSAGE_THROW_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/message-throw.png", customClassLoader));
			MESSAGE_CATCH_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/message.png", customClassLoader));
			SIGNAL_THROW_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/signal-throw.png", customClassLoader));
			SIGNAL_CATCH_IMAGE = ImageIO
					.read(ReflectUtil.getResource("org/flowable/icons/signal.png", customClassLoader));
			/*
			 * String baseUrl = Thread.currentThread().getContextClassLoader().getResource(
			 * "static/img/activiti/").getPath(); SCRIPTTASK_IMAGE = ImageIO.read(new
			 * FileInputStream(baseUrl+"scriptTask.png")); USERTASK_IMAGE = ImageIO.read(new
			 * FileInputStream(baseUrl+"userTask.png")); SERVICETASK_IMAGE =
			 * ImageIO.read(new FileInputStream(baseUrl+"serviceTask.png"));
			 * RECEIVETASK_IMAGE = ImageIO.read(new
			 * FileInputStream(baseUrl+"receiveTask.png")); SENDTASK_IMAGE =
			 * ImageIO.read(new FileInputStream(baseUrl+"sendTask.png")); MANUALTASK_IMAGE =
			 * ImageIO.read(new FileInputStream(baseUrl+"manualTask.png"));
			 * BUSINESS_RULE_TASK_IMAGE = ImageIO.read(new
			 * FileInputStream(baseUrl+"businessRuleTask.png")); SHELL_TASK_IMAGE =
			 * ImageIO.read(new FileInputStream(baseUrl+"shellTask.png")); CAMEL_TASK_IMAGE
			 * = ImageIO.read(new FileInputStream(baseUrl+"camelTask.png")); MULE_TASK_IMAGE
			 * = ImageIO.read(new FileInputStream(baseUrl+"muleTask.png")); TIMER_IMAGE =
			 * ImageIO.read(new FileInputStream(baseUrl+"timer.png"));
			 * COMPENSATE_THROW_IMAGE = ImageIO.read(new
			 * FileInputStream(baseUrl+"compensate-throw.png")); COMPENSATE_CATCH_IMAGE =
			 * ImageIO.read(new FileInputStream(baseUrl+"compensate.png"));
			 * ERROR_THROW_IMAGE = ImageIO.read(new
			 * FileInputStream(baseUrl+"error-throw.png")); ERROR_CATCH_IMAGE =
			 * ImageIO.read(new FileInputStream(baseUrl+"error.png")); MESSAGE_THROW_IMAGE =
			 * ImageIO.read(new FileInputStream(baseUrl+"message-throw.png"));
			 * MESSAGE_CATCH_IMAGE = ImageIO.read(new
			 * FileInputStream(baseUrl+"message.png")); SIGNAL_THROW_IMAGE =
			 * ImageIO.read(new FileInputStream(baseUrl+"signal-throw.png"));
			 * SIGNAL_CATCH_IMAGE = ImageIO.read(new FileInputStream(baseUrl+"signal.png"));
			 */
			flag = true;
		} catch (IOException e) {
			flag = false;
			LOGGER.warn("Could not load image for process diagram creation: {}", e.getMessage());
		}
	}

}