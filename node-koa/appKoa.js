const Koa = require('koa');
const bodyParser = require('koa-bodyparser');
const Router = require('koa-router');
const { addUser } = require("./logic")


var app = new Koa();
var router = new Router();
app.use(bodyParser());

router
  .post('/api/users', async (ctx, next) => {
    try {
      const resp = await addUser(ctx.request.body);
      ctx.response.body = resp
    } catch (error) {
      if (error.message.indexOf('Error inserting into database') || error.message.indexOf('Error counting rows')) {
        ctx.response.status = 500;  
      } else {
        ctx.response.status = 400;  
      }

      ctx.response.body= { message: error.message}
    }
    
  })
app
  .use(router.routes())
  .use(router.allowedMethods());


const port = process.env.PORT || 3000; // set our port
app.use(router.routes());
app.listen(port);



