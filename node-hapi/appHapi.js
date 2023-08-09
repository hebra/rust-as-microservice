const Hapi = require('@hapi/hapi')
const {addUser} = require('./logic.js')

const init = async () => {

        const server = Hapi.server({
            port: 3000,
            host: 'localhost'
        });

        server.route({
            method: 'POST',
            path: '/api/users',
            handler: async (request, resp) => {
                try {
                    const respBody = await addUser(request.payload)
                    return {
                        body: respBody
                    }
                } catch (error) {
                    if (error.message === 'Error inserting into database' || error.message === 'Error counting rows') {
                        return resp.response(error.message).code(500)
                    } else {
                        return resp.response(error.message).code(400)
                    }

                }
            }
        })

        await server.start();
        console.log('Server running on %s', server.info.uri);
    }
;

process.on('unhandledRejection', (err) => {
    console.log(err);
    process.exit(1);
});

init();
