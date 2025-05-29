import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class FreeDrawing extends Shape implements Serializable {
    private ArrayList<Point> points;
    
    public FreeDrawing(Color color, int x1, int y1, int x2, int y2, boolean filled) {
        super(color, x1, y1, x2, y2, filled);
        points = new ArrayList<>();
        points.add(new Point(x1, y1));
        points.add(new Point(x2, y2));
    }
    
    public void addPoint(int x, int y) {
        points.add(new Point(x, y));
        // 更新边界框
        x1 = Math.min(x1, x);
        y1 = Math.min(y1, y);
        x2 = Math.max(x2, x);
        y2 = Math.max(y2, y);
    }
    
    @Override
    public void setEndPoint(int x, int y) {
        super.setEndPoint(x, y);
        addPoint(x, y);
    }
    
    @Override
    public void draw(Graphics g) {
        if (points.size() < 2) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        
        drawSelectionHandles(g);
    }
    
    @Override
    public void move(int dx, int dy) {
        super.move(dx, dy);
        for (Point p : points) {
            p.x += dx;
            p.y += dy;
        }
    }
    
    @Override
    public boolean containsPoint(int x, int y) {
        // 自由绘图的点击检测，检查点是否在路径附近
        for (int i = 0; i < points.size() - 1; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get(i + 1);
            
            // 计算点到线段的距离
            double distance = pointToLineDistance(x, y, p1.x, p1.y, p2.x, p2.y);
            if (distance < 5) {  // 5像素容差
                return true;
            }
        }
        return false;
    }
    
    private double pointToLineDistance(int x, int y, int x1, int y1, int x2, int y2) {
        double normalLength = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
        if (normalLength == 0) return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        
        double t = ((x - x1) * (x2 - x1) + (y - y1) * (y2 - y1)) / (normalLength * normalLength);
        
        if (t < 0) return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
        if (t > 1) return Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
        
        double projX = x1 + t * (x2 - x1);
        double projY = y1 + t * (y2 - y1);
        
        return Math.sqrt((x - projX) * (x - projX) + (y - projY) * (y - projY));
    }
} 