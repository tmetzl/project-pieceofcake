package org.pieceofcake.gui.controllers;

import java.util.List;
import java.util.Set;

import org.pieceofcake.gui.utils.ResizableCanvas;
import org.pieceofcake.objects.Location;
import org.pieceofcake.streetnetwork.DiGraph;
import org.pieceofcake.streetnetwork.Edge;
import org.pieceofcake.streetnetwork.Node;

import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class MapTabController {

	private static final double ARR_SIZE = 4;
	private static final double BORDER_DISTANCE = 20;
	private static final double ICON_SIZE = 15;

	private DiGraph streetNetwork;
	private double[] dimensions;
	private double widthFactor;
	private double heightFactor;

	@FXML
	private ResizableCanvas mapCanvas;
	@FXML
	private HBox mapContainer;

	@FXML
	private void initialize() {
		mapContainer.widthProperty().addListener(event -> resizeCanvas());
		mapContainer.heightProperty().addListener(event -> resizeCanvas());
	}

	public void setStreetNetwork(DiGraph streetNetwork) {
		this.streetNetwork = streetNetwork;
		getDimensions();
		updateConversionFactors();
		draw();
	}

	private void resizeCanvas() {
		mapCanvas.setWidth(mapContainer.getWidth());
		mapCanvas.setHeight(mapContainer.getHeight());
		mapCanvas.getGraphicsContext2D().clearRect(0, 0, mapCanvas.getWidth(), mapCanvas.getHeight());
		updateConversionFactors();
		draw();
	}

	private void getDimensions() {
		if (streetNetwork != null) {
			Set<Node> nodes = streetNetwork.getNodes();
			// xMin, xMax, yMin, yMax
			double[] dim = { 0d, 0d, 0d, 0d };
			for (Node node : nodes) {
				Location location = node.getLocation();
				double x = location.getX();
				double y = location.getY();
				if (x < dim[0]) {
					dim[0] = x;
				} else if (x > dim[1]) {
					dim[1] = x;
				}
				if (y < dim[2]) {
					dim[2] = y;
				} else if (y > dim[3]) {
					dim[3] = y;
				}
			}
			this.dimensions = dim;
		}
	}

	private void updateConversionFactors() {
		double canvasWidth = mapCanvas.getWidth() - BORDER_DISTANCE * 2;
		double canvasHeight = mapCanvas.getHeight() - BORDER_DISTANCE * 2;

		double mapWidth = dimensions[1] - dimensions[0];
		double mapHeight = dimensions[3] - dimensions[2];

		widthFactor = canvasWidth / mapWidth;
		heightFactor = canvasHeight / mapHeight;
	}

	private double[] convertToCanvasCoordinates(double x, double y) {
		return new double[] { BORDER_DISTANCE + (x - dimensions[0]) * widthFactor,
				BORDER_DISTANCE + (y - dimensions[2]) * heightFactor };
	}

	private void draw() {
		drawNodes();
		drawEdges();
	}

	private void drawNodes() {
		GraphicsContext gc = mapCanvas.getGraphicsContext2D();
		Set<Node> nodes = streetNetwork.getNodes();
		for (Node node : nodes) {
			double[] coordinates = convertToCanvasCoordinates(node.getLocation().getX(), node.getLocation().getY());

			if (node.getType().equals("customer")) {
				gc.setFill(Color.LIGHTBLUE);
			} else if (node.getType().equals("bakery")) {
				gc.setFill(Color.LIGHTGREEN);
			} else {
				gc.setFill(Color.BLACK);
			}
			gc.fillOval(coordinates[0] - ICON_SIZE / 2, coordinates[1] - ICON_SIZE / 2, ICON_SIZE, ICON_SIZE);
			gc.setFill(Color.BLACK);
			gc.strokeOval(coordinates[0] - ICON_SIZE / 2, coordinates[1] - ICON_SIZE / 2, ICON_SIZE, ICON_SIZE);
		}
	}

	private void drawEdges() {
		Set<Node> nodes = streetNetwork.getNodes();
		for (Node node : nodes) {
			List<Edge> edges = streetNetwork.getEdges(node.getGuid());
			for (Edge edge : edges) {
				double[] coordinatesFrom = convertToCanvasCoordinates(edge.getFrom().getLocation().getX(),
						edge.getFrom().getLocation().getY());
				double[] coordinatesTo = convertToCanvasCoordinates(edge.getTo().getLocation().getX(),
						edge.getTo().getLocation().getY());
				drawArrow(coordinatesFrom, coordinatesTo);
			}
		}
	}

	private double[] findIntersection(double[] from, double[] to) {
		double m = (to[1] - from[1]) / (to[0] - from[0]);
		double b = (from[1] - m * from[0]);
		double r = ICON_SIZE / 2;

		double[] intersectingPointsStart = getIntersectionCircleLine(m, b, from[0], from[1], r);
		double[] intersectingPointsEnd = getIntersectionCircleLine(m, b, to[0], to[1], r);

		double xMin = Math.min(from[0], to[0]);
		double xMax = Math.max(from[0], to[0]);
		double xStart;
		double yStart;
		double xEnd;
		double yEnd;
		if (xMin <= intersectingPointsStart[0] && intersectingPointsStart[0] <= xMax) {
			xStart = intersectingPointsStart[0];
			yStart = intersectingPointsStart[1];
		} else {
			xStart = intersectingPointsStart[2];
			yStart = intersectingPointsStart[3];
		}
		if (xMin <= intersectingPointsEnd[0] && intersectingPointsEnd[0] <= xMax) {
			xEnd = intersectingPointsEnd[0];
			yEnd = intersectingPointsEnd[1];
		} else {
			xEnd = intersectingPointsEnd[2];
			yEnd = intersectingPointsEnd[3];
		}
		return new double[] { xStart, yStart, xEnd, yEnd };
	}

	private double[] getIntersectionCircleLine(double m, double b, double xM, double yM, double r) {
		double factor = 1d / (m * m + 1d);
		double constant = -b * m + m * yM + xM;
		double constant2 = Math.sqrt(-b * b - 2 * b * m * xM + 2 * b * yM + m * m * r * r - m * m * xM * xM
				+ 2 * m * xM * yM + r * r - yM * yM);
		double x1 = factor * (constant - constant2);
		double x2 = factor * (constant + constant2);
		return new double[] { x1, m * x1 + b, x2, m * x2 + b };
	}

	private void drawArrow(double[] from, double[] to) {
		GraphicsContext gc = mapCanvas.getGraphicsContext2D();

		double[] coordinates = findIntersection(from, to);

		double dx = coordinates[0] - coordinates[2];
		double dy = coordinates[1] - coordinates[3];
		double angle = Math.atan2(dy, dx);
		double len = Math.sqrt(dx * dx + dy * dy);

		Transform transform = Transform.translate(coordinates[2], coordinates[3]);
		transform = transform.createConcatenation(Transform.rotate(Math.toDegrees(angle), 0, 0));
		gc.setTransform(new Affine(transform));

		gc.strokeLine(0, 0, len, 0);
		gc.fillPolygon(new double[] { len, len - ARR_SIZE, len - ARR_SIZE, len },
				new double[] { 0, -ARR_SIZE, ARR_SIZE, 0 }, 4);
		gc.setTransform(new Affine());
	}

}
