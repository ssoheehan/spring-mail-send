@Controller
@RequestMapping("/api/mail/")
public class MailController {
    private static final Logger logger = LoggerFactory.getLogger(MailController.class);
	private CompositeConfiguration config;

    @Autowired
    private MailService service;

    OutputStream fileOut = null;

    //메일발송 페이지
    @RequestMapping(value = "/mail-management", method = RequestMethod.GET)
    public String getMailMng(Model model) {
            return "/mail/mail_mng";
        }

    @RequestMapping(value = "/members/send", method = RequestMethod.POST)
    @ResponseBody
    public Object postSendMail(@RequestBody AdminDTO AdminDTO, HttpServletRequest request) throws Exception{
        return service.sendMail(maAdminDTO);
    }
}
