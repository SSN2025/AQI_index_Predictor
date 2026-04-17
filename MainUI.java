import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainUI {

    private JFrame frame;
    private JLabel statusLabel;
    private JLabel resultLabel;
    private JTextField dateInput;
    private GraphPanel graphPanel;

    // Data State (stored so we can switch graph views without re-calculating)
    private List<Double> fullTrajectory;
    private int totalHistorySize;

    // Custom Neo Dark Colors
    private final Color BACKGROUND_COLOR = new Color(15, 15, 19);
    private final Color PANEL_COLOR = new Color(22, 22, 28);
    private final Color NEON_CYAN = new Color(0, 255, 204);
    private final Color TEXT_WHITE = new Color(255, 255, 255);
    private final Color TEXT_MUTED = new Color(138, 145, 153);
    private final Color ERROR_RED = new Color(255, 60, 90);

    public static void main(String[] args){
        SwingUtilities.invokeLater(()->{
            try{
                MainUI window = new MainUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MainUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("AQI Forecasting System");
        frame.setBounds(100, 100, 950, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(BACKGROUND_COLOR);
        frame.getContentPane().setLayout(new BorderLayout());

        // --- TOP PANEL (Input & Controls) ---
        JPanel topPanel = new JPanel();
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("TARGET DATE (YYYY-MM-DD): ");
        titleLabel.setForeground(TEXT_MUTED);
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        
        dateInput = new JTextField("2026-03-29", 10);
        dateInput.setFont(new Font("Consolas", Font.PLAIN, 16));
        dateInput.setBackground(PANEL_COLOR);
        dateInput.setForeground(NEON_CYAN);
        dateInput.setCaretColor(NEON_CYAN);
        dateInput.setBorder(BorderFactory.createLineBorder(TEXT_MUTED));

        JButton forecastButton = new JButton("EXECUTE FORECAST");
        styleButton(forecastButton, NEON_CYAN, BACKGROUND_COLOR);
        forecastButton.addActionListener(e -> runForecastEngine());

        topPanel.add(titleLabel);
        topPanel.add(dateInput);
        topPanel.add(Box.createHorizontalStrut(15));
        topPanel.add(forecastButton);

        frame.getContentPane().add(topPanel, BorderLayout.NORTH);

        // --- CENTER PANEL (The Graph & Time Filters) ---
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setBackground(BACKGROUND_COLOR);
        centerWrapper.setBorder(new EmptyBorder(0, 30, 0, 30));

        // Time Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(BACKGROUND_COLOR);
        
        JButton btnWeek = new JButton("Last 1 Week");
        JButton btnMonth = new JButton("Last 1 Month");
        JButton btnYear = new JButton("Last 1 Year");
        JButton btnAll = new JButton("All Time");

        styleFilterButton(btnWeek, 7);
        styleFilterButton(btnMonth, 30);
        styleFilterButton(btnYear, 365);
        styleFilterButton(btnAll, -1);

        filterPanel.add(new JLabel("<html><font color='#8a9199'>Graph View: </font></html>"));
        filterPanel.add(btnWeek);
        filterPanel.add(btnMonth);
        filterPanel.add(btnYear);
        filterPanel.add(btnAll);

        graphPanel = new GraphPanel();
        graphPanel.setBackground(PANEL_COLOR);
        graphPanel.setBorder(BorderFactory.createLineBorder(new Color(40, 40, 45), 1));
        
        centerWrapper.add(filterPanel, BorderLayout.NORTH);
        centerWrapper.add(graphPanel, BorderLayout.CENTER);
        
        frame.getContentPane().add(centerWrapper, BorderLayout.CENTER);

        // --- BOTTOM PANEL (Results) ---
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BACKGROUND_COLOR);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(20, 20, 30, 20));

        resultLabel = new JLabel("--");
        resultLabel.setForeground(NEON_CYAN);
        resultLabel.setFont(new Font("Consolas", Font.BOLD, 48));
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        statusLabel = new JLabel("System Ready.");
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setFont(new Font("Consolas", Font.PLAIN, 14));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(resultLabel);
        bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        bottomPanel.add(statusLabel);

        frame.getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton btn, Color fg, Color bg) {
        btn.setFont(new Font("Consolas", Font.BOLD, 14));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleFilterButton(JButton btn, int historyDaysToKeep) {
        btn.setFont(new Font("Consolas", Font.PLAIN, 12));
        btn.setForeground(TEXT_WHITE);
        btn.setBackground(new Color(40, 40, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> updateGraphView(historyDaysToKeep));
    }

    private void runForecastEngine() {
        String dateStr = dateInput.getText().trim();
        LocalDate targetDate;

        try {
            targetDate = LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            updateStatus("ERR: Invalid date format. Use YYYY-MM-DD", ERROR_RED);
            return;
        }

        LocalDate dbEndDate = LocalDate.of(2025, 12, 31);
        if (!targetDate.isAfter(dbEndDate)) {
            updateStatus("ERR: Target date must be after " + dbEndDate, ERROR_RED);
            return;
        }

        updateStatus("Fetching database records and training AR Model...", TEXT_MUTED);
        resultLabel.setText("...");
        graphPanel.clearData();

        new Thread(() -> {
            try {
                AQIDataProvider dataProvider = new MySQLDataFetcher();
                List<AQIRecord> data = dataProvider.fetchData();

                if (data.isEmpty()) {
                    updateStatus("ERR: No data retrieved. Check database connection.", ERROR_RED);
                    return;
                }

                totalHistorySize = data.size();
                ARModelTrainer trainer = new ARModelTrainer(7);
                ARModel trainedModel = trainer.train(data);

                ForecastEngine engine = new ForecastEngine(trainedModel, data);
                
                // Get the full trajectory of data
                fullTrajectory = engine.runForecast(dbEndDate, targetDate);
                double finalPrediction = fullTrajectory.get(fullTrajectory.size() - 1);

                // Update UI with results and default graph (Last 1 Year)
                SwingUtilities.invokeLater(() -> {
                    updateStatus("Forecast Complete. Target Date: " + targetDate, TEXT_MUTED);
                    resultLabel.setText(String.format("%.2f AQI", finalPrediction));
                    updateGraphView(365); // Default to 1 Year View
                });

            } catch (Exception ex) {
                updateStatus("System Failure: " + ex.getMessage(), ERROR_RED);
            }
        }).start();
    }

    // Method to slice the data and update the graph based on the chosen time filter
    private void updateGraphView(int historyDaysToKeep) {
        if (fullTrajectory == null) return;

        List<Double> viewData = new ArrayList<>();
        int newHistoryCutoff;

        if (historyDaysToKeep == -1 || historyDaysToKeep >= totalHistorySize) {
            // All time
            viewData.addAll(fullTrajectory);
            newHistoryCutoff = totalHistorySize;
        } else {
            // Sliced view
            int startIndex = totalHistorySize - historyDaysToKeep;
            for (int i = startIndex; i < fullTrajectory.size(); i++) {
                viewData.add(fullTrajectory.get(i));
            }
            newHistoryCutoff = historyDaysToKeep;
        }

        graphPanel.setData(viewData, newHistoryCutoff);
    }

    private void updateStatus(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
        });
    }

    // --- CUSTOM 2D GRAPHING COMPONENT ---
    class GraphPanel extends JPanel {
        private List<Double> dataPoints;
        private int historyCutoff;

        public void setData(List<Double> dataPoints, int historyCutoff) {
            this.dataPoints = dataPoints;
            this.historyCutoff = historyCutoff;
            this.repaint();
        }

        public void clearData() {
            this.dataPoints = null;
            this.repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dataPoints == null || dataPoints.isEmpty()) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int paddingX = 60; // Extra padding for Y-axis text
            int paddingY = 40;

            double maxData = Collections.max(dataPoints);
            double minData = Collections.min(dataPoints);
            maxData += (maxData - minData) * 0.1; // 10% headroom

            double xScale = (double) (width - 2 * paddingX) / (dataPoints.size() - 1);
            double yScale = (double) (height - 2 * paddingY) / (maxData - minData);

            // 1. Draw Grid Lines and Y-Axis Labels
            g2.setFont(new Font("Consolas", Font.PLAIN, 12));
            for (int i = 0; i <= 4; i++) {
                int y = paddingY + i * (height - 2 * paddingY) / 4;
                g2.setColor(new Color(40, 40, 45));
                g2.drawLine(paddingX, y, width - paddingX, y); // Grid line
                
                // Y-Axis AQI labels
                g2.setColor(TEXT_MUTED);
                double aqiVal = maxData - (i * (maxData - minData) / 4);
                g2.drawString(String.format("%.0f", aqiVal), paddingX - 35, y + 5);
            }

            // 2. Draw Paths
            Path2D historyPath = new Path2D.Double();
            Path2D predictionPath = new Path2D.Double();

            for (int i = 0; i < dataPoints.size(); i++) {
                double x = paddingX + i * xScale;
                double y = height - paddingY - ((dataPoints.get(i) - minData) * yScale);

                if (i == 0) {
                    historyPath.moveTo(x, y);
                } else if (i < historyCutoff) {
                    historyPath.lineTo(x, y);
                    if (i == historyCutoff - 1) predictionPath.moveTo(x, y); 
                } else {
                    predictionPath.lineTo(x, y);
                }
            }

            // Render History Line (Muted Gray)
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(new Color(100, 105, 115, 180)); 
            g2.draw(historyPath);

            // Render Prediction Line (Neon Cyan)
            g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(NEON_CYAN);
            g2.draw(predictionPath);
            
            // 3. Mark "Today" (End of history, start of prediction)
            double transX = paddingX + (historyCutoff - 1) * xScale;
            double transY = height - paddingY - ((dataPoints.get(historyCutoff - 1) - minData) * yScale);
            g2.setColor(Color.YELLOW);
            g2.fillOval((int) transX - 4, (int) transY - 4, 8, 8);
            g2.setColor(TEXT_WHITE);
            g2.drawString("Forecast Start", (int) transX - 40, (int) transY - 10);

            // 4. Mark "Final Target"
            double finalX = paddingX + (dataPoints.size() - 1) * xScale;
            double finalY = height - paddingY - ((dataPoints.get(dataPoints.size() - 1) - minData) * yScale);
            g2.setColor(NEON_CYAN);
            g2.fillOval((int) finalX - 5, (int) finalY - 5, 10, 10);

            // 5. Draw Legend (Top Right Box)
            int legendX = width - paddingX - 140;
            int legendY = paddingY;
            g2.setColor(new Color(30, 30, 35, 200));
            g2.fillRect(legendX, legendY - 20, 140, 55);
            g2.setColor(TEXT_MUTED);
            g2.drawRect(legendX, legendY - 20, 140, 55);

            g2.setColor(new Color(100, 105, 115));
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(legendX + 10, legendY, legendX + 30, legendY);
            g2.setColor(TEXT_WHITE);
            g2.drawString("Historical", legendX + 40, legendY + 5);

            g2.setColor(NEON_CYAN);
            g2.drawLine(legendX + 10, legendY + 20, legendX + 30, legendY + 20);
            g2.setColor(TEXT_WHITE);
            g2.drawString("Predicted", legendX + 40, legendY + 25);
        }
    }
}