package org.terasology.codecity.world.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.terasology.codecity.world.facet.CodeCityFacet;
import org.terasology.codecity.world.map.CodeMap;
import org.terasology.codecity.world.map.CodeMapFactory;
import org.terasology.codecity.world.map.DrawableCode;
import org.terasology.codecity.world.map.MapObject;
import org.terasology.codecity.world.structure.CodeClass;
import org.terasology.codecity.world.structure.CodePackage;
import org.terasology.codecity.world.structure.scale.CodeScale;
import org.terasology.codecity.world.structure.scale.SquaredCodeScale;
import org.terasology.math.ChunkMath;
import org.terasology.math.Rect2i;
import org.terasology.math.Vector2i;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.Updates;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

import com.sun.jna.platform.unix.X11.Drawable;

/**
 * Creates a new surface for buildings using an squared scale and a CodeMap.
 * This surface is created just above the ground.
 * @author alstrat
 *
 */

@Produces(CodeCityFacet.class)
@Requires(@Facet(SurfaceHeightFacet.class))
public class CodeCityBuildingProvider implements FacetProvider {
	
	private CodeMap codeMap;

    private final CodeScale scale = new SquaredCodeScale();
    private final CodeMapFactory factory = new CodeMapFactory(scale);
	
	public CodeCityBuildingProvider() {
		CodeClass c = new CodeClass(10, 1500);
		CodeClass c2 = new CodeClass(50, 150);
	    CodePackage p3 = new CodePackage();
	    CodePackage p1 = new CodePackage();
	    CodePackage p2 = new CodePackage();
	    p1.addCodeContent(c2);
	    p1.addCodeContent(c2);
	    p1.addCodeContent(c2);
	    p1.addCodeContent(c2);
	    p2.addCodeContent(c);
	    p3.addCodeContent(p1);
	    p3.addCodeContent(p2);

	    List<DrawableCode> code = new ArrayList<DrawableCode>();
	    code.add(p1.getDrawableCode());
	    code.add(p2.getDrawableCode());
	    code.add(p2.getDrawableCode());
	    code.add(p2.getDrawableCode());
	    code.add(p2.getDrawableCode());
	    code.add(p3.getDrawableCode());

	    codeMap = factory.generateMap(code);
	}

    @Override
    public void setSeed(long seed) {

    }
    /**
     * Updates the height of the indicated position given a CodeMap.
     * @param facet Surface where the height should be updated.
     * @param codeMap Map with the height information.
     * @param position Position to update its height.
     * @param fase Fase from the origin to ensure the correct position of the map in the world.
     * @param level Height where the map should be placed.
     * @param base Ground height.
     */
    private void processPosition(CodeCityFacet facet, CodeMap codeMap, Vector2i position, Vector2i fase,int level, int base){
        if(codeMap.isUsed(position.x-fase.x, position.y-fase.y)){
            MapObject mapObject = codeMap.getMapObject(position.x-fase.x, position.y-fase.y);
            int height = level+base+mapObject.getHeight(scale, factory);
            facet.setWorld(position.x, position.y, height);
            
            //Obtains the relative position of the map.
            DrawableCode drawable = mapObject.getObject();
            Vector2i auxPosition = codeMap.getCodePosition(drawable);
            
            //Calculated the relative position where its sub-map should be placed
            fase = new Vector2i(fase.x+auxPosition.x+1, fase.y+auxPosition.y+1);
            
            //Recursion
            CodeMap auxMap = drawable.getSubmap(scale, factory);
            processPosition(facet, auxMap, position, fase, level+1, base);
        }
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(CodeCityFacet.class);
        int base = (int) region.getRegionFacet(SurfaceHeightFacet.class).get(0, 0);
        CodeCityFacet facet = new CodeCityFacet(region.getRegion(),
                border, base);
        Rect2i processRegion = facet.getWorldRegion();
        //Assigns the height to every position in the region
        for (Vector2i position : processRegion) {
            processPosition(facet, codeMap, position, Vector2i.zero(),  0, base);
            
        }
        // give our newly created and populated facet to the region
        region.setRegionFacet(CodeCityFacet.class, facet);
    }

}
