package net.java.dev.moskito.core.accumulation;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.java.dev.moskito.core.dynamic.OnDemandStatsProducer;
import net.java.dev.moskito.core.helper.TieableDefinition;
import net.java.dev.moskito.core.helper.TieableRepository;
import net.java.dev.moskito.core.producers.IStats;
import net.java.dev.moskito.core.producers.IStatsProducer;

/**
 * Repository that holds and manages accumulators.
 * @author lrosenberg
 *
 */
public class AccumulatorRepository extends TieableRepository<Accumulator> {
	/**
	 * The singleton instance.
	 */
	private static final AccumulatorRepository INSTANCE = new AccumulatorRepository();
	/**
	 * Map that contains names of the accumulators maped by ids.
	 */
	private ConcurrentMap<String, String> id2nameMapping = new ConcurrentHashMap<String, String>();
	/**
	 * Returns the singleton instance of the AccumulatorRepository.
	 * @return
	 */
	public static final AccumulatorRepository getInstance(){
		return INSTANCE;
	}

	@Override
	protected boolean tie(Accumulator acc, IStatsProducer producer) {
		AccumulatorDefinition definition = acc.getDefinition();
		IStats target = null;
		for (IStats s : producer.getStats()){
			if (s.getName().equals(definition.getStatName())){
				target = s;
				break;
			}
		}
		
		if (target==null){
			if (producer instanceof OnDemandStatsProducer){
				addToAutoTie(acc, producer);
			}else{
				throw new IllegalArgumentException("StatObject not found "+definition.getStatName()+" in "+definition);
			}
		}

		acc.tieToStats(target);
		return true;
		
	}
	
	@Override
	protected Accumulator create(TieableDefinition def){
		return new Accumulator((AccumulatorDefinition)def);
	}
	/**
	 * Returns configured accumulators.
	 * @return
	 */
	public List<Accumulator> getAccumulators(){
		return getTieables();
	}

	/**
	 * Creates a new accumulator out of given definition.
	 * @param definition
	 * @return
	 */
	public Accumulator createAccumulator(AccumulatorDefinition definition) {
		Accumulator ret = createTieable(definition);
		id2nameMapping.put(ret.getId(), ret.getName());
		return ret;
	}
	
	/**
	 * Returns the accumulator with the corresponding id.
	 * @param id
	 * @return
	 */
	public Accumulator getAccumulatorById(String id){
		String name = id2nameMapping.get(id);
		if (name==null)
			throw new IllegalArgumentException("Id: "+id+" not bound");
		return getByName(name);
	}

}