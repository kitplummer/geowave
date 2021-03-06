package mil.nga.giat.geowave.analytics.kmeans;

import mil.nga.giat.geowave.analytics.clustering.CentroidPairing;
import mil.nga.giat.geowave.analytics.distance.DistanceFn;
import mil.nga.giat.geowave.analytics.tools.AnalyticItemWrapper;

/**
 * Compute the distance of a points to the closest centroid, providing the
 * resulting distance using a provided distance function.
 * 
 */
public class CentroidAssociationFn<T>
{
	private DistanceFn<T> distanceFunction;

	public DistanceFn<T> getDistanceFunction() {
		return distanceFunction;
	}

	public void setDistanceFunction(
			final DistanceFn<T> distanceFunction ) {
		this.distanceFunction = distanceFunction;
	}

	public double compute(
			final AnalyticItemWrapper<T> point,
			final Iterable<AnalyticItemWrapper<T>> targetSet,
			final AssociationNotification<T> associationNotification ) {
		final CentroidPairing<T> pairing = new CentroidPairing<T>(
				null,
				point,
				Double.POSITIVE_INFINITY);
		for (final AnalyticItemWrapper<T> y : targetSet) {
			final double distance = distanceFunction.measure(
					point.getWrappedItem(),
					y.getWrappedItem());
			if (distance < pairing.getDistance()) {
				pairing.setDistance(distance);
				pairing.setCentroid(y);
			}
		}
		associationNotification.notify(pairing);
		return pairing.getDistance();
	}

	public double compute(
			final Iterable<AnalyticItemWrapper<T>> pointSet,
			final Iterable<AnalyticItemWrapper<T>> targetSet,
			final AssociationNotification<T> associationNotification ) {
		double sum = 0.0;
		for (final AnalyticItemWrapper<T> point : pointSet) {
			sum += this.compute(
					point,
					targetSet,
					associationNotification);
		}
		return sum;
	}
}
