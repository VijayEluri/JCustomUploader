package uploader;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class UploaderPanel extends JPanel {
    private static final int MARGIN_SIZE = 5;
    public static final Color BG_COLOR = Color.WHITE;
    private static final ImageIcon ICON_ADD = Util.createImageIcon("/resources/add.png");
    private static final ImageIcon ICON_ADD_PRESSED = Util.createImageIcon("/resources/add-press.png");
    private static final ImageIcon ICON_ADD_HOVER = Util.createImageIcon("/resources/add-hover.png");
    private static final ImageIcon ICON_UPLOAD_PAUSE = Util.createImageIcon("/resources/upload-pause.png");
    private static final ImageIcon ICON_UPLOAD_PAUSE_PRESSED = Util.createImageIcon("/resources/upload-pause-press.png");
    private static final ImageIcon ICON_UPLOAD_PAUSE_HOVER = Util.createImageIcon("/resources/upload-pause-hover.png");
    private static final ImageIcon ICON_UPLOAD_RESUME = Util.createImageIcon("/resources/upload-resume.png");
    private static final ImageIcon ICON_UPLOAD_RESUME_PRESSED = Util.createImageIcon("/resources/upload-resume-press.png");
    private static final ImageIcon ICON_UPLOAD_RESUME_HOVER = Util.createImageIcon("/resources/upload-resume-hover.png");
    private static final ImageIcon ICON_CLEAR = Util.createImageIcon("/resources/clear.png");
    private static final ImageIcon ICON_CLEAR_PRESSED = Util.createImageIcon("/resources/clear-press.png");
    private static final ImageIcon ICON_CLEAR_HOVER = Util.createImageIcon("/resources/clear-hover.png");
    private static final ImageIcon ICON_RETRY = Util.createImageIcon("/resources/retry.png");
    private static final ImageIcon ICON_RETRY_PRESSED = Util.createImageIcon("/resources/retry-press.png");
    private static final ImageIcon ICON_RETRY_HOVER = Util.createImageIcon("/resources/retry-hover.png");

    private static final JFileChooser FC;
    private static final ImageFileFilter FILTER_IMAGES = new ImageFileFilter();
    private static final ImagePreview IMAGE_PREVIEW_ACCESSORY;
    static {
        System.setProperty("swing.disableFileChooserSpeedFix", "true");
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        FC = new JFileChooser();
        FC.setAcceptAllFileFilterUsed(false);
        FC.setMultiSelectionEnabled(true);
        FC.setFileFilter(FILTER_IMAGES);
        IMAGE_PREVIEW_ACCESSORY = new ImagePreview(FC);
    }

    private final JPanel pnlUploadList = new JPanel();
    private final JLabel txtPending = new JLabel("Nothing to upload yet.");
    private final JLabel txtUploaded = new JLabel("No photos uploaded yet.");
    private final JButton btnRetryFailed = new JButton("Retry all 999 failed uploads", ICON_RETRY);
    private final JButton btnClearCompleted = new JButton("Clear completed uploads", ICON_CLEAR);
    private final UploadManager uploader;
    private boolean uploadingEnabled = true;

    public UploaderPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(new EmptyBorder(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE));
        this.setBackground(BG_COLOR);

        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        uploader = new UploadManager(this, new TestUploadMechanism(250, 0.25, 0.10));

        add(create_commands_panel());
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(create_upload_list());
        add(create_footer_panel());

        uploader.start();
    }

    private JPanel create_commands_panel() {
        JPanel pnlCmds = new JPanel();
        pnlCmds.setLayout(new BoxLayout(pnlCmds, BoxLayout.X_AXIS));
        pnlCmds.setOpaque(false);
        pnlCmds.setAlignmentX(Component.LEFT_ALIGNMENT);

        final JButton btnAddImages = new JButton("Add images", ICON_ADD);
        Util.prepButtonUI(btnAddImages, ICON_ADD_PRESSED, ICON_ADD_HOVER);
        pnlCmds.add(btnAddImages);
        btnAddImages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FC.setFileSelectionMode(JFileChooser.FILES_ONLY);
                FC.setDialogTitle("Choose image files to upload");
                FC.setAccessory(IMAGE_PREVIEW_ACCESSORY);
                int ret = FC.showDialog(btnAddImages, "Upload");
                if(ret == JFileChooser.APPROVE_OPTION) {
                    // TODO: remove these example entries
                    for(File f : FC.getSelectedFiles()) {
                        uploader.addFileToUpload(f);
                    }
                }
            }
        });

        pnlCmds.add(Box.createRigidArea(new Dimension(15, 0)));

        JButton btnAddFolder = new JButton("Add all images in a folder", ICON_ADD);
        Util.prepButtonUI(btnAddFolder, ICON_ADD_PRESSED, ICON_ADD_HOVER);
        pnlCmds.add(btnAddFolder);
        btnAddFolder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                FC.setDialogTitle("Choose folders to upload images from");
                FC.setAccessory(null);
                int ret = FC.showDialog(btnAddImages, "Upload");
                if(ret == JFileChooser.APPROVE_OPTION) {
                    for(File dir : FC.getSelectedFiles()) {
                        for(File f : dir.listFiles()) {
                            if(!f.isDirectory() && FILTER_IMAGES.accept(f)) {
                                uploader.addFileToUpload(f);
                            }
                        }
                    }
                }
            }
        });

        pnlCmds.add(Box.createHorizontalGlue());

        final JButton btnToggleUploading = new JButton("Pause uploading", ICON_UPLOAD_PAUSE);
        Util.prepButtonUI(btnToggleUploading, ICON_UPLOAD_PAUSE_PRESSED, ICON_UPLOAD_PAUSE_HOVER);
        btnToggleUploading.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uploadingEnabled = !uploadingEnabled;
                uploader.setUploadingEnabled(uploadingEnabled);
                if(uploadingEnabled) {
                    btnToggleUploading.setText("Pause uploading");
                    btnToggleUploading.setIcon(ICON_UPLOAD_PAUSE);
                    btnToggleUploading.setPressedIcon(ICON_UPLOAD_PAUSE_PRESSED);
                    btnToggleUploading.setRolloverIcon(ICON_UPLOAD_PAUSE_HOVER);
                }
                else {
                    btnToggleUploading.setText("Resume uploading");
                    btnToggleUploading.setIcon(ICON_UPLOAD_RESUME);
                    btnToggleUploading.setPressedIcon(ICON_UPLOAD_RESUME_PRESSED);
                    btnToggleUploading.setRolloverIcon(ICON_UPLOAD_RESUME_HOVER);
                }
            }
        });
        pnlCmds.add(btnToggleUploading);
        pnlCmds.add(Box.createRigidArea(new Dimension(1, 0)));

        return pnlCmds;
    }

    private JScrollPane create_upload_list() {
        pnlUploadList.setLayout(new BoxLayout(pnlUploadList, BoxLayout.Y_AXIS));
        pnlUploadList.setBackground(BG_COLOR);
        pnlUploadList.setAlignmentX(Component.LEFT_ALIGNMENT);

        JScrollPane spUploadList = new JScrollPane(pnlUploadList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        spUploadList.setAlignmentX(Component.LEFT_ALIGNMENT);
        return spUploadList;
    }

    private JPanel create_footer_panel() {
        JPanel pnlFooter = new JPanel();
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlFooter.setLayout(new BoxLayout(pnlFooter, BoxLayout.Y_AXIS));

        Util.prepButtonUI(btnRetryFailed, ICON_RETRY_PRESSED, ICON_RETRY_HOVER);
        Util.prepButtonUI(btnClearCompleted, ICON_CLEAR_PRESSED, ICON_CLEAR_HOVER);

        // make each of the buttons the same size
        Dimension btnSz = new Dimension(Math.max(btnClearCompleted.getMaximumSize().width, btnRetryFailed.getMaximumSize().width),
                                        Math.max(btnClearCompleted.getMaximumSize().height, btnRetryFailed.getMaximumSize().height));
        Util.setSize(btnClearCompleted, btnSz);
        Util.setSize(btnRetryFailed, btnSz);

        // hide until clicking them would have some effect
        btnClearCompleted.setVisible(false);
        btnRetryFailed.setVisible(false);

        btnRetryFailed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uploader.retryFailedItems();
            }
        });

        btnClearCompleted.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uploader.clearCompletedItems();
            }
        });

        txtPending.setVerticalAlignment(SwingConstants.CENTER);
        txtUploaded.setVerticalAlignment(SwingConstants.CENTER);
        txtPending.setAlignmentY(Component.CENTER_ALIGNMENT);
        txtUploaded.setAlignmentY(Component.CENTER_ALIGNMENT);

        pnlFooter.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlFooter.add(create_footer_panel_top());
        pnlFooter.add(Box.createRigidArea(new Dimension(0, 5)));
        pnlFooter.add(create_footer_panel_btm());

        return pnlFooter;
    }

    private JPanel create_footer_panel_top() {
        JPanel pnlFT = new JPanel();
        pnlFT.setOpaque(false);
        pnlFT.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlFT.setLayout(new BoxLayout(pnlFT, BoxLayout.X_AXIS));
        pnlFT.add(txtPending);
        pnlFT.add(Box.createHorizontalGlue());
        pnlFT.add(btnRetryFailed);
        pnlFT.add(Box.createRigidArea(new Dimension(1, btnRetryFailed.getMaximumSize().height)));
        return pnlFT;
    }

    private JPanel create_footer_panel_btm() {
        JPanel pnlFB = new JPanel();
        pnlFB.setOpaque(false);
        pnlFB.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlFB.setLayout(new BoxLayout(pnlFB, BoxLayout.X_AXIS));
        pnlFB.add(txtUploaded);
        pnlFB.add(Box.createHorizontalGlue());
        pnlFB.add(btnClearCompleted);
        pnlFB.add(Box.createRigidArea(new Dimension(1, btnClearCompleted.getMaximumSize().height)));
        return pnlFB;
    }

    /** returns the component which can clear completed items */
    public Component getUIClear() {
        return btnClearCompleted;
    }

    /** returns the component which can put failed items back in the upload queue */
    public Component getUIRetry() {
        return btnRetryFailed;
    }

    /** returns the container which holds upload items */
    public Container getUploadItemsContainer() {
        return pnlUploadList;
    }

    /** sets the UI components which show pending and completed upload info */
    public void setProgressTexts(String pending, String completed) {
        txtPending.setText(pending);
        txtUploaded.setText(completed);
    }

    /** sets the number of outstanding failures */
    public void setNumberFailures(int n) {
        if(n > 0) {
            if(n == 1)
                btnRetryFailed.setText("Retry the failed upload");
            else
                btnRetryFailed.setText("Retry all " + n + " failed uploads");
            btnRetryFailed.setVisible(true);
        }
        else
            btnRetryFailed.setVisible(false);
    }
}
