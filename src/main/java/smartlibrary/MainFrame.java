package smartlibrary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Swing desktop interface; business rules and SQL remain in their own layers. */
public class MainFrame extends JFrame {
    private static final Color WINE = new Color(91, 24, 43);
    private static final Color WINE_LIGHT = new Color(126, 43, 62);
    private static final Color PAPER = new Color(247, 243, 237);
    private static final Color INK = new Color(42, 34, 36);
    private static final Color MUTED = new Color(119, 105, 105);
    private static final Color RULE = new Color(229, 218, 211);
    private final LibraryService service;
    private final LibraryRepository repo;
    private final JTabbedPane tabs=new JTabbedPane();
    private final JLabel[] stats=new JLabel[5];
    private JTable booksTable,authorsTable,membersTable,loansTable;
    private JTextField searchField;
    private JComboBox<String> genreFilter;
    private JComboBox<String> loanView;
    private boolean updatingFilters;
    private final String[] statNames={"Book titles","Copies available","Members","Active loans","Overdue"};

    public MainFrame(LibraryService service) {
        super("Smart Library Management System");this.service=service;this.repo=service.repository();
        applyTheme();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);setMinimumSize(new Dimension(1050,680));setSize(1180,760);setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());getContentPane().setBackground(PAPER);
        getContentPane().add(header(),BorderLayout.NORTH);
        tabs.addTab("Dashboard",dashboardPanel());tabs.addTab("Books",booksPanel());tabs.addTab("Authors",authorsPanel());tabs.addTab("Members",membersPanel());tabs.addTab("Loans & Reports",loansPanel());
        tabs.addChangeListener(e->{try{refreshCurrent();}catch(Exception ex){showError(ex);}});
        getContentPane().add(tabs,BorderLayout.CENTER);
        try { refreshAll(); } catch(Exception ex) { showError(ex); }
    }

    private void applyTheme() {
        UIManager.put("Panel.background", PAPER); UIManager.put("OptionPane.background", PAPER);
        UIManager.put("OptionPane.messageForeground", INK); UIManager.put("Label.foreground", INK);
        UIManager.put("Button.background", Color.WHITE); UIManager.put("Button.foreground", WINE);
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 13));
        UIManager.put("TextField.background", Color.WHITE); UIManager.put("TextField.foreground", INK);
        UIManager.put("ComboBox.background", Color.WHITE); UIManager.put("ComboBox.foreground", INK);
        UIManager.put("TabbedPane.background", PAPER); UIManager.put("TabbedPane.foreground", INK);
        UIManager.put("Table.background", Color.WHITE); UIManager.put("Table.foreground", INK);
        UIManager.put("Table.selectionBackground", new Color(244, 226, 230));
        UIManager.put("Table.selectionForeground", WINE);
    }

    private JComponent header() {
        JPanel p=new JPanel(new BorderLayout());p.setBackground(WINE);p.setBorder(new EmptyBorder(19,28,19,28));
        JLabel title=new JLabel("SMART LIBRARY");title.setForeground(new Color(255,248,244));title.setFont(new Font("Serif",Font.BOLD,24));
        JLabel subtitle=new JLabel("Management System  ·  Java + MySQL");subtitle.setForeground(new Color(229,201,207));subtitle.setFont(new Font("SansSerif",Font.PLAIN,13));
        JPanel labels=new JPanel(new GridLayout(2,1));labels.setOpaque(false);labels.add(title);labels.add(subtitle);p.add(labels,BorderLayout.WEST);
        JButton refresh=new JButton("Refresh");styleButton(refresh,true);refresh.addActionListener(e->{try{refreshAll();}catch(Exception ex){showError(ex);}});p.add(refresh,BorderLayout.EAST);return p;
    }

    private JPanel dashboardPanel() {
        JPanel p=new JPanel(new BorderLayout(18,18));p.setBorder(new EmptyBorder(26,28,28,28));p.setBackground(PAPER);
        JPanel heading=new JPanel(new BorderLayout(4,5));heading.setOpaque(false);
        JLabel title=new JLabel("Library at a glance");title.setForeground(INK);title.setFont(new Font("Serif",Font.BOLD,27));
        JLabel subtitle=new JLabel("A current view of the catalogue, members, and circulation.");subtitle.setForeground(MUTED);subtitle.setFont(new Font("SansSerif",Font.PLAIN,14));
        heading.add(title,BorderLayout.NORTH);heading.add(subtitle,BorderLayout.SOUTH);p.add(heading,BorderLayout.NORTH);

        JPanel content=new JPanel(new BorderLayout(17,18));content.setOpaque(false);
        JPanel cards=new JPanel(new GridLayout(1,5,13,13));cards.setOpaque(false);
        for(int i=0;i<stats.length;i++) {
            JPanel card=new JPanel(new BorderLayout(4,8));card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(RULE),new EmptyBorder(0,16,16,16)));
            JPanel accent=new JPanel();accent.setBackground(i==4?WINE_LIGHT:WINE);accent.setPreferredSize(new Dimension(1,4));card.add(accent,BorderLayout.NORTH);
            stats[i]=new JLabel("—");stats[i].setForeground(i==4?WINE_LIGHT:WINE);stats[i].setFont(new Font("Serif",Font.BOLD,29));
            JLabel label=new JLabel(statNames[i]);label.setForeground(MUTED);label.setFont(new Font("SansSerif",Font.PLAIN,12));
            card.add(stats[i],BorderLayout.CENTER);card.add(label,BorderLayout.SOUTH);cards.add(card);
        }
        content.add(cards,BorderLayout.NORTH);

        JPanel information=new JPanel(new GridLayout(1,2,16,16));information.setOpaque(false);
        information.add(dashboardInfoCard("LIBRARY WORKFLOW",
                "Use Books to maintain and search the catalogue, Members to register borrowers, and Loans & Reports to issue, return, and review loans."));
        information.add(dashboardInfoCard("LOAN GUIDELINES",
                "Each loan lasts 14 days. Overdue status updates from the current database date, with a late fine of ₹5 per day."));
        content.add(information,BorderLayout.CENTER);p.add(content,BorderLayout.CENTER);return p;
    }

    private JPanel dashboardInfoCard(String eyebrow,String text) {
        JPanel card=new JPanel(new BorderLayout(8,12));card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(RULE),new EmptyBorder(18,20,20,20)));
        JLabel label=new JLabel(eyebrow);label.setForeground(WINE);label.setFont(new Font("SansSerif",Font.BOLD,11));
        JTextArea body=new JTextArea(text);body.setEditable(false);body.setFocusable(false);body.setLineWrap(true);body.setWrapStyleWord(true);
        body.setForeground(INK);body.setFont(new Font("SansSerif",Font.PLAIN,14));body.setBackground(Color.WHITE);body.setBorder(null);
        card.add(label,BorderLayout.NORTH);card.add(body,BorderLayout.CENTER);return card;
    }

    private JPanel booksPanel() {
        JPanel p=basePanel();JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField=new JTextField(22);searchField.setToolTipText("Search title or author");genreFilter=new JComboBox<>(new String[]{"All genres"});
        JButton search=new JButton("Search");search.addActionListener(e->{try{refreshBooks();}catch(Exception ex){showError(ex);}});
        searchField.addActionListener(e->search.doClick());genreFilter.addActionListener(e->{if(!updatingFilters)try{refreshBooks();}catch(Exception ex){showError(ex);}});
        controls.add(new JLabel("Title / author"));controls.add(searchField);controls.add(new JLabel("Genre"));controls.add(genreFilter);controls.add(search);p.add(controls,BorderLayout.NORTH);
        booksTable=new JTable();p.add(new JScrollPane(booksTable),BorderLayout.CENTER);
        JPanel buttons=new JPanel(new FlowLayout(FlowLayout.LEFT));buttons.add(button("Add Book",e->editBook(null)));buttons.add(button("Edit Selected",e->editBook(selectedRow(booksTable))));buttons.add(button("Delete Selected",e->deleteSelected(booksTable,"book")));p.add(buttons,BorderLayout.SOUTH);return p;
    }

    private JPanel authorsPanel() {
        JPanel p=basePanel();authorsTable=new JTable();p.add(new JScrollPane(authorsTable),BorderLayout.CENTER);JPanel b=new JPanel(new FlowLayout(FlowLayout.LEFT));
        b.add(button("Add Author",e->editAuthor(null)));b.add(button("Edit Selected",e->editAuthor(selectedRow(authorsTable))));b.add(button("Delete Selected",e->deleteSelected(authorsTable,"author")));p.add(b,BorderLayout.SOUTH);return p;
    }

    private JPanel membersPanel() {
        JPanel p=basePanel();membersTable=new JTable();p.add(new JScrollPane(membersTable),BorderLayout.CENTER);JPanel b=new JPanel(new FlowLayout(FlowLayout.LEFT));
        b.add(button("Register Member",e->editMember(null)));b.add(button("Edit Selected",e->editMember(selectedRow(membersTable))));b.add(button("Delete Selected",e->deleteSelected(membersTable,"member")));p.add(b,BorderLayout.SOUTH);return p;
    }

    private JPanel loansPanel() {
        JPanel p=basePanel();JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT));loanView=new JComboBox<>(new String[]{"Active Loans","Overdue Loans","Loan History"});
        loanView.addActionListener(e->{try{refreshLoans();}catch(Exception ex){showError(ex);}});controls.add(new JLabel("Report"));controls.add(loanView);controls.add(button("Issue Book",e->issueBook()));
        controls.add(button("Return Selected Loan",e->returnLoan()));controls.add(button("Refresh",e->{try{refreshLoans();refreshStats();}catch(Exception ex){showError(ex);}}));p.add(controls,BorderLayout.NORTH);
        loansTable=new JTable();p.add(new JScrollPane(loansTable),BorderLayout.CENTER);JLabel hint=new JLabel("Loan period: 14 days  ·  Late fine: ₹5 per day  ·  Overdue status is updated automatically");hint.setBorder(new EmptyBorder(8,4,4,4));p.add(hint,BorderLayout.SOUTH);return p;
    }

    private JPanel basePanel(){JPanel p=new JPanel(new BorderLayout(10,14));p.setBackground(PAPER);p.setBorder(new EmptyBorder(18,24,20,24));return p;}
    private JButton button(String label,java.awt.event.ActionListener action){JButton b=new JButton(label);styleButton(b,label.startsWith("Add")||label.startsWith("Register")||label.equals("Issue Book"));b.addActionListener(action);return b;}
    private void styleButton(JButton b,boolean primary){b.setFocusPainted(false);b.setOpaque(true);b.setBackground(primary?WINE:Color.WHITE);b.setForeground(primary?Color.WHITE:WINE);b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(primary?WINE:RULE),new EmptyBorder(7,13,7,13)));}

    private void refreshCurrent() throws SQLException {
        switch(tabs.getSelectedIndex()){case 0:refreshStats();break;case 1:refreshBooks();break;case 2:refreshAuthors();break;case 3:refreshMembers();break;case 4:refreshLoans();break;default:break;}
    }
    private void refreshAll() throws SQLException {refreshStats();refreshAuthors();refreshBooks();refreshMembers();refreshLoans();}
    private void refreshStats() throws SQLException {int[] v=repo.getDashboardStats();for(int i=0;i<v.length;i++)stats[i].setText(String.valueOf(v[i]));}
    private void refreshAuthors() throws SQLException {setTable(authorsTable,new String[]{"ID","Name","Country"},repo.getAuthors());}
    private void refreshBooks() throws SQLException {
        if(booksTable==null)return;
        String selected=(String)genreFilter.getSelectedItem();
        List<String> genres=repo.getGenres();String keep=selected;updatingFilters=true;try{genreFilter.removeAllItems();genreFilter.addItem("All genres");for(String g:genres)genreFilter.addItem(g);if(keep!=null)genreFilter.setSelectedItem(keep);}finally{updatingFilters=false;}
        setTable(booksTable,new String[]{"ID","Title","Author","Genre","Total Copies","Available"},repo.getBooks(searchField.getText(),(String)genreFilter.getSelectedItem()));
    }
    private void refreshMembers() throws SQLException {setTable(membersTable,new String[]{"ID","Name","City","Join Date","Email"},repo.getMembers());}
    private void refreshLoans() throws SQLException {if(loanView!=null)setTable(loansTable,new String[]{"Loan ID","Member","Book","Issue Date","Due Date","Return Date","Fine (₹)"},repo.getLoans((String)loanView.getSelectedItem()));}
    private void setTable(JTable table,String[] columns,List<Object[]> rows){if(table==null)return;DefaultTableModel m=new DefaultTableModel(columns,0){@Override public boolean isCellEditable(int r,int c){return false;}};for(Object[] row:rows)m.addRow(row);table.setModel(m);table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);table.setAutoCreateRowSorter(true);table.setRowHeight(30);table.setShowGrid(false);table.setIntercellSpacing(new Dimension(0,1));table.setFillsViewportHeight(true);table.setFont(new Font("SansSerif",Font.PLAIN,13));table.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,12));table.getTableHeader().setBackground(new Color(244,237,232));table.getTableHeader().setForeground(WINE);table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,RULE));table.getTableHeader().setPreferredSize(new Dimension(0,34));table.setBorder(null);JScrollPane scroll=(JScrollPane)SwingUtilities.getAncestorOfClass(JScrollPane.class,table);if(scroll!=null){scroll.setBorder(BorderFactory.createLineBorder(RULE));scroll.getViewport().setBackground(Color.WHITE);}}

    private Object[] selectedRow(JTable table){if(table==null||table.getSelectedRow()<0){JOptionPane.showMessageDialog(this,"Select a row first.");return null;}int row=table.convertRowIndexToModel(table.getSelectedRow());Object[] out=new Object[table.getColumnCount()];for(int i=0;i<out.length;i++)out[i]=table.getModel().getValueAt(row,i);return out;}
    private void editAuthor(Object[] row){
        JTextField name=new JTextField(row==null?"":String.valueOf(row[1]));JTextField country=new JTextField(row==null?"":String.valueOf(row[2]));Object[] fields={"Name",name,"Country",country};
        if(JOptionPane.showConfirmDialog(this,fields,row==null?"Add Author":"Edit Author",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)try{service.saveAuthor(row==null?null:Integer.valueOf(row[0].toString()),name.getText(),country.getText());refreshAuthors();refreshBooks();refreshStats();}catch(Exception ex){showError(ex);}
    }
    private void editBook(Object[] row){
        try {
            List<Object[]> authors=repo.getAuthors();if(authors.isEmpty()){JOptionPane.showMessageDialog(this,"Add an author before adding a book.");return;}
            JComboBox<AuthorChoice> author=new JComboBox<>();int authorId=row==null?-1:authorIdForBook(row[0].toString());for(Object[] a:authors){AuthorChoice choice=new AuthorChoice(Integer.parseInt(a[0].toString()),a[1]+" · "+a[2]);author.addItem(choice);if(choice.id==authorId)author.setSelectedItem(choice);}
            JTextField title=new JTextField(row==null?"":String.valueOf(row[1]));JTextField genre=new JTextField(row==null?"Fiction":String.valueOf(row[3]));JSpinner total=new JSpinner(new SpinnerNumberModel(row==null?1:Integer.parseInt(row[4].toString()),1,10000,1));
            int available=row==null?-1:Integer.parseInt(row[5].toString());int borrowed=row==null?0:Integer.parseInt(row[4].toString())-available;
            Object[] fields={"Title",title,"Author",author,"Genre",genre,"Total copies",total,row==null?"Available copies will equal total copies":"Copies currently borrowed",row==null?"0":String.valueOf(borrowed)};
            if(JOptionPane.showConfirmDialog(this,fields,row==null?"Add Book":"Edit Book",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){int newTotal=(Integer)total.getValue();int newAvailable=row==null?newTotal:newTotal-borrowed;service.saveBook(row==null?null:Integer.valueOf(row[0].toString()),title.getText(),((AuthorChoice)author.getSelectedItem()).id,genre.getText(),newTotal,newAvailable);refreshBooks();refreshStats();}
        }catch(Exception ex){showError(ex);}
    }
    private int authorIdForBook(String id)throws SQLException{for(Object[] b:repo.getBooks("", "All genres")){if(b[0].toString().equals(id))for(Object[] a:repo.getAuthors())if((a[1]+" · "+a[2]).equals(b[2]))return Integer.parseInt(a[0].toString());}return -1;}
    private void editMember(Object[] row){
        JTextField name=new JTextField(row==null?"":String.valueOf(row[1]),22);
        JTextField city=new JTextField(row==null?"":String.valueOf(row[2]),22);
        JTextField date=new JTextField(row==null?LocalDate.now().toString():String.valueOf(row[3]),22);
        JTextField email=new JTextField(row==null||row[4]==null?"":String.valueOf(row[4]),22);
        date.setToolTipText("Enter a date in YYYY-MM-DD format");
        JPanel form=new JPanel(new GridLayout(0,2,12,12));form.setBackground(PAPER);form.setBorder(new EmptyBorder(12,12,12,12));
        form.add(formLabel("Member name"));form.add(name);
        form.add(formLabel("City"));form.add(city);
        form.add(formLabel("Join date (YYYY-MM-DD)"));form.add(date);
        form.add(formLabel("Email (optional)"));form.add(email);
        int result=JOptionPane.showConfirmDialog(this,form,row==null?"Register Member":"Edit Member",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(result!=JOptionPane.OK_OPTION)return;
        try {
            service.saveMember(row==null?null:Integer.valueOf(row[0].toString()),name.getText(),city.getText(),LocalDate.parse(date.getText().trim()),email.getText());
            refreshMembers();refreshStats();
            JOptionPane.showMessageDialog(this,row==null?"Member registered successfully.":"Member details updated.","Library System",JOptionPane.INFORMATION_MESSAGE);
        } catch(Exception ex) { showError(ex); }
    }

    private JLabel formLabel(String text){JLabel label=new JLabel(text);label.setForeground(INK);label.setFont(new Font("SansSerif",Font.PLAIN,13));return label;}

    private void deleteSelected(JTable table,String entity){Object[] row=selectedRow(table);if(row==null)return;if(JOptionPane.showConfirmDialog(this,"Delete this "+entity+"? Records already referenced by loans or books cannot be deleted.","Confirm delete",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)return;try{int id=Integer.parseInt(row[0].toString());if("author".equals(entity))repo.deleteAuthor(id);else if("book".equals(entity))repo.deleteBook(id);else repo.deleteMember(id);refreshAll();}catch(Exception ex){showError(ex);}}

    private void issueBook(){
        try {
            List<Object[]> books=repo.getBooks("","All genres");List<Object[]> members=repo.getMembers();
            JComboBox<BookChoice> bookBox=new JComboBox<>();for(Object[] b:books)if(Integer.parseInt(b[5].toString())>0)bookBox.addItem(new BookChoice(Integer.parseInt(b[0].toString()),b[1]+" (available: "+b[5]+")"));
            JComboBox<MemberChoice> memberBox=new JComboBox<>();for(Object[] m:members)memberBox.addItem(new MemberChoice(Integer.parseInt(m[0].toString()),m[1]+" · "+m[2]));
            if(bookBox.getItemCount()==0||memberBox.getItemCount()==0){JOptionPane.showMessageDialog(this,"An available book and at least one member are required.");return;}
            Object[] fields={"Book",bookBox,"Member",memberBox};if(JOptionPane.showConfirmDialog(this,fields,"Issue Book",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){service.issue(((BookChoice)bookBox.getSelectedItem()).id,((MemberChoice)memberBox.getSelectedItem()).id);refreshAll();JOptionPane.showMessageDialog(this,"Book issued. It is due in 14 days.");}
        }catch(Exception ex){showError(ex);}
    }
    private void returnLoan(){Object[] row=selectedRow(loansTable);if(row==null)return;try{BigDecimal fine=repo.returnLoan(Integer.parseInt(row[0].toString()));refreshAll();JOptionPane.showMessageDialog(this,"Book returned. Fine assessed: ₹"+fine.setScale(2));}catch(Exception ex){showError(ex);}}

    private void showError(Exception ex){String message=ex.getMessage();if(ex instanceof SQLException&&message!=null&&message.contains("Communications link failure"))message="Could not connect to MySQL. Start the server and check the database settings.\n\n"+message;JOptionPane.showMessageDialog(this,message==null?ex.toString():message,"Library System",JOptionPane.ERROR_MESSAGE);}
    private static class AuthorChoice {final int id;final String label;AuthorChoice(int id,String label){this.id=id;this.label=label;}public String toString(){return label;}}
    private static class BookChoice {final int id;final String label;BookChoice(int id,String label){this.id=id;this.label=label;}public String toString(){return label;}}
    private static class MemberChoice {final int id;final String label;MemberChoice(int id,String label){this.id=id;this.label=label;}public String toString(){return label;}}
}
