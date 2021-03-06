/**
 *
 */
package space.exploration.mars.rover.environment;

import space.exploration.mars.rover.environment.Wall.IllegalWallDefinitionException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author sanketkorgaonkar
 */
public class WallBuilder extends VirtualElement {
    /**
     *
     */
    private static final long serialVersionUID = -8073507793314915381L;

    private static final int WALL_DEF_DIMENSION = 4;

    private List<Wall> walls        = null;
    private Properties matrixConfig = null;

    public WallBuilder(Properties matrixConfig) {
        this.matrixConfig = matrixConfig;
        this.walls = new ArrayList<Wall>();
        build();
    }

    @Override
    public void draw(Graphics2D g2) {
        for (int i = 0; i < walls.size(); i++) {
            walls.get(i).draw(g2);
        }
    }

    @Override
    public void build() {
        super.setMatrixConfig(matrixConfig);
        super.setLayout();
        int wallNum = Integer.parseInt(matrixConfig.getProperty(EnvironmentUtils.NUM_WALLS_PROPERTY));

        for (int i = 0; i < wallNum; i++) {
            int[]  wallDef   = getWallDefinition(i);
            String wallColor = getWallColor(i);
            Wall   wall      = new Wall(matrixConfig);
            try {
                wall.setDefinition(wallDef, wallColor);
            } catch (IllegalWallDefinitionException e) {
                e.printStackTrace();
            }
            walls.add(wall);
        }
    }

    @Override
    public Color getColor() {
        return null;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    private String getWallColor(int wallNo) {
        String[] wallProps = matrixConfig.getProperty(EnvironmentUtils.WALL_DEFS_PROPERTY + Integer.toString(wallNo))
                .split(",");
        return wallProps[4];
    }

    private int[] getWallDefinition(int wallNo) {
        int[] wallDef = new int[WALL_DEF_DIMENSION];
        String[] wallProps = matrixConfig.getProperty(EnvironmentUtils.WALL_DEFS_PROPERTY + Integer.toString(wallNo))
                .split(",");
        for (int i = 0; i < wallDef.length; i++) {
            wallDef[i] = Integer.parseInt(wallProps[i]);
        }
        return wallDef;
    }

    @Override
    public void paint(Graphics g) {
        draw((Graphics2D) g);
    }
}
